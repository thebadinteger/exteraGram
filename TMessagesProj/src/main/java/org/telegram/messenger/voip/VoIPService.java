}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) throws Exception {
		File fl = new File(filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		fin.close();
		return ret;
	}

	public boolean hasRate() {
		return needRateCall || forceRating;
	}

	private void onTgVoipStop(Instance.FinalState finalState) {
		if (user == null || privateCall == null || finalState == null) {
			return;
		}
		if (TextUtils.isEmpty(finalState.debugLog)) {
			try {
				finalState.debugLog = getStringFromFile(VoIPHelper.getLogFilePath("" + privateCall.id, true));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		final MessagesController messagesController = MessagesController.getInstance(currentAccount);
		if (messagesController.voipDebug == null) {
			messagesController.voipDebug = new VoIPDebugToSend(currentAccount);
		}
		messagesController.voipDebug.push(privateCall.id, privateCall.access_hash, finalState, lastLogFilePath);
		lastLogFilePath = null;

		if (needSendDebugLog) {
			messagesController.voipDebug.done(privateCall.id, needSendDebugLog);
			needSendDebugLog = false;
		}
	}

	private void initializeAccountRelatedThings() {
		updateServerConfig();
		NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.appDidLogout);
		ConnectionsManager.getInstance(currentAccount).setAppPaused(false, false);
	}

	@SuppressLint("InvalidWakeLockTag")
	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildVars.LOGS_ENABLED) {
			FileLog.d("=============== VoIPService STARTING ===============");
		}
		try {
			AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER) != null) {
				int outFramesPerBuffer = Integer.parseInt(am.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));
				Instance.setBufferSize(outFramesPerBuffer);
			} else {
				Instance.setBufferSize(AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT) / 2);
			}

			cpuWakelock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "telegram-voip");
			cpuWakelock.acquire();

			btAdapter = am.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;

			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			if (!USE_CONNECTION_SERVICE) {
				filter.addAction(ACTION_HEADSET_PLUG);
				if (btAdapter != null) {
					filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
					filter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
				}
				filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
				filter.addAction(Intent.ACTION_SCREEN_ON);
				filter.addAction(Intent.ACTION_SCREEN_OFF);
			}
			registerReceiver(receiver, filter);
			fetchBluetoothDeviceName();

			if (audioDeviceCallback == null) {
				try {
					audioDeviceCallback = new AudioDeviceCallback() {
						@Override
						public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
							checkUpdateBluetoothHeadset();
						}

						@Override
						public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
							checkUpdateBluetoothHeadset();
						}
					};
				} catch (Throwable e) {
					//java.lang.NoClassDefFoundError on some devices
					FileLog.e(e);
					audioDeviceCallback = null;
				}
			}
			if (audioDeviceCallback != null) {
				am.registerAudioDeviceCallback(audioDeviceCallback, new Handler(Looper.getMainLooper()));
			}
			am.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));

			checkUpdateBluetoothHeadset();
		} catch (Exception x) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.e("error initializing voip controller", x);
			}
			callFailed();
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (callIShouldHavePutIntoIntent != null) {
				NotificationsController.checkOtherNotificationsChannel();
				Notification.Builder bldr = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL)
						.setContentTitle(LocaleController.getString(R.string.VoipOutgoingCall))
						.setShowWhen(false);
				if (groupCall != null) {
					bldr.setSmallIcon(isMicMute() ? R.drawable.voicechat_muted : R.drawable.voicechat_active);
				} else {
					bldr.setSmallIcon(R.drawable.call);
				}
				foregroundStarted = true;
				if (Build.VERSION.SDK_INT >= 33) {
					startForeground(foregroundId = ID_ONGOING_CALL_NOTIFICATION, foregroundNotification = bldr.build(), lastForegroundType = getCurrentForegroundType());
				} else {
					startForeground(foregroundId = ID_ONGOING_CALL_NOTIFICATION, foregroundNotification = bldr.build());
				}
			} else {
				NotificationsController.checkOtherNotificationsChannel();
				Notification.Builder bldr = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL)
						.setContentTitle(LocaleController.getString(R.string.VoipCallEnded))
						.setShowWhen(false);
				bldr.setSmallIcon(R.drawable.call);
				foregroundStarted = true;
				if (Build.VERSION.SDK_INT >= 33) {
					startForeground(foregroundId = ID_ONGOING_CALL_NOTIFICATION, foregroundNotification = bldr.build(), lastForegroundType = getCurrentForegroundType());
				} else {
					startForeground(foregroundId = ID_ONGOING_CALL_NOTIFICATION, foregroundNotification = bldr.build());
				}
			}
		}
	}

	private void checkUpdateBluetoothHeadset() {
		if (!USE_CONNECTION_SERVICE && btAdapter != null && btAdapter.isEnabled()) {
			try {
				MediaRouter mr = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
				AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (Build.VERSION.SDK_INT < 24) {
					int headsetState = btAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
					updateBluetoothHeadsetState(headsetState == BluetoothProfile.STATE_CONNECTED);
					for (StateListener l : stateListeners) {
						l.onAudioSettingsChanged();
					}
				} else {
					MediaRouter.RouteInfo ri = mr.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO);
					if (PermissionRequest.hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && ri.getDeviceType() == MediaRouter.RouteInfo.DEVICE_TYPE_BLUETOOTH) {
						int headsetState = btAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
						updateBluetoothHeadsetState(headsetState == BluetoothProfile.STATE_CONNECTED);
						for (StateListener l : stateListeners) {
							l.onAudioSettingsChanged();
						}
					} else {
						updateBluetoothHeadsetState(am.isBluetoothA2dpOn());
					}
				}
			} catch (Throwable e) {
				FileLog.e(e);
			}
		}
	}

	private void loadResources() {
		if (Build.VERSION.SDK_INT >= 21) {
			WebRtcAudioTrack.setAudioTrackUsageAttribute(AudioAttributes.USAGE_VOICE_COMMUNICATION);
		}
		Utilities.globalQueue.postRunnable(() -> {
			soundPool = new SoundPool(1, AudioManager.STREAM_VOICE_CALL, 0);
			spConnectingId = soundPool.load(this, R.raw.voip_connecting, 1);
			spRingbackID = soundPool.load(this, R.raw.voip_ringback, 1);
			spFailedID = soundPool.load(this, R.raw.voip_failed, 1);
			spEndId = soundPool.load(this, R.raw.voip_end, 1);
			spBusyId = soundPool.load(this, R.raw.voip_busy, 1);
			spVoiceChatEndId = soundPool.load(this, R.raw.voicechat_leave, 1);
			spVoiceChatStartId = soundPool.load(this, R.raw.voicechat_join, 1);
			spVoiceChatConnecting = soundPool.load(this, R.raw.voicechat_connecting, 1);
			spAllowTalkId = soundPool.load(this, R.raw.voip_onallowtalk, 1);
			spStartRecordId = soundPool.load(this, R.raw.voip_recordstart, 1);
		});
	}

	private void dispatchStateChanged(int state) {
		if (BuildVars.LOGS_ENABLED) {
			FileLog.d("== Call " + getCallID() + " state changed to " + state + " ==");
		}
		currentState = state;
		if (currentState == STATE_ESTABLISHED) {
			destroyConverting();
		}
		if (USE_CONNECTION_SERVICE && state == STATE_ESTABLISHED /*&& !wasEstablished*/ && systemCallConnection != null) {
			systemCallConnection.setActive();
		}
		for (int a = 0; a < stateListeners.size(); a++) {
			StateListener l = stateListeners.get(a);
			l.onStateChanged(state);
		}
	}

	private void updateTrafficStats(NativeInstance instance, Instance.TrafficStats trafficStats) {
		if (instance == null) return;
		if (trafficStats == null) {
			trafficStats = instance.getTrafficStats();
		}
		if (trafficStats == null) return;
		final long wifiSentDiff = trafficStats.bytesSentWifi - (prevTrafficStats != null ? prevTrafficStats.bytesSentWifi : 0);
		final long wifiRecvdDiff = trafficStats.bytesReceivedWifi - (prevTrafficStats != null ? prevTrafficStats.bytesReceivedWifi : 0);
		final long mobileSentDiff = trafficStats.bytesSentMobile - (prevTrafficStats != null ? prevTrafficStats.bytesSentMobile : 0);
		final long mobileRecvdDiff = trafficStats.bytesReceivedMobile - (prevTrafficStats != null ? prevTrafficStats.bytesReceivedMobile : 0);
		prevTrafficStats = trafficStats;
		if (wifiSentDiff > 0) {
			StatsController.getInstance(currentAccount).incrementSentBytesCount(StatsController.TYPE_WIFI, StatsController.TYPE_CALLS, wifiSentDiff);
		}
		if (wifiRecvdDiff > 0) {
			StatsController.getInstance(currentAccount).incrementReceivedBytesCount(StatsController.TYPE_WIFI, StatsController.TYPE_CALLS, wifiRecvdDiff);
		}
		if (mobileSentDiff > 0) {
			StatsController.getInstance(currentAccount).incrementSentBytesCount(lastNetInfo != null && lastNetInfo.isRoaming() ? StatsController.TYPE_ROAMING : StatsController.TYPE_MOBILE, StatsController.TYPE_CALLS, mobileSentDiff);
		}
		if (mobileRecvdDiff > 0) {
			StatsController.getInstance(currentAccount).incrementReceivedBytesCount(lastNetInfo != null && lastNetInfo.isRoaming() ? StatsController.TYPE_ROAMING : StatsController.TYPE_MOBILE, StatsController.TYPE_CALLS, mobileRecvdDiff);
		}
	}

	@SuppressLint("InvalidWakeLockTag")
	private void configureDeviceForCall() {
		if (BuildVars.LOGS_ENABLED) {
			FileLog.d("configureDeviceForCall, route to set = " + audioRouteToSet);
		}

		if (Build.VERSION.SDK_INT >= 21) {
			WebRtcAudioTrack.setAudioTrackUsageAttribute(hasRtmpStream() ? AudioAttributes.USAGE_MEDIA : AudioAttributes.USAGE_VOICE_COMMUNICATION);
			WebRtcAudioTrack.setAudioStreamType(hasRtmpStream() ? AudioManager.USE_DEFAULT_STREAM_TYPE : AudioManager.STREAM_VOICE_CALL);
		}

		needPlayEndSound = true;
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (!USE_CONNECTION_SERVICE) {
			Utilities.globalQueue.postRunnable(() -> {
				try {
					if (hasRtmpStream()) {
						am.setMode(AudioManager.MODE_NORMAL);
						am.setBluetoothScoOn(false);
						AndroidUtilities.runOnUIThread(() -> {
							if (!MediaController.getInstance().isMessagePaused()) {
								MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
							}
						});
						return;
					}

					am.setMode(AudioManager.MODE_IN_COMMUNICATION);
				} catch (Exception e) {
					FileLog.e(e);
				}
				AndroidUtilities.runOnUIThread(() -> {
					int focusResult = am.requestAudioFocus(VoIPService.this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
					hasAudioFocus = focusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
					final VoipAudioManager vam = VoipAudioManager.get();
					if (isBluetoothHeadsetConnected() && hasEarpiece()) {
						switch (audioRouteToSet) {
							case AUDIO_ROUTE_BLUETOOTH:
								if (!bluetoothScoActive) {
									needSwitchToBluetoothAfterScoActivates = true;
									try {
										am.startBluetoothSco();
									} catch (Throwable e) {
										FileLog.e(e);
									}
								} else {
									am.setBluetoothScoOn(true);
									vam.setSpeakerphoneOn(false);
								}
								break;
							case AUDIO_ROUTE_EARPIECE:
								am.setBluetoothScoOn(false);
								vam.setSpeakerphoneOn(false);
								break;
							case AUDIO_ROUTE_SPEAKER:
								am.setBluetoothScoOn(false);
								vam.setSpeakerphoneOn(true);
								break;
						}
					} else if (isBluetoothHeadsetConnected()) {
						am.setBluetoothScoOn(speakerphoneStateToSet);
					} else {
						vam.setSpeakerphoneOn(speakerphoneStateToSet);
						if (speakerphoneStateToSet) {
							audioRouteToSet = AUDIO_ROUTE_SPEAKER;
						} else {
							audioRouteToSet = AUDIO_ROUTE_EARPIECE;
						}
						if (lastSensorEvent != null) {
							//For the case when the phone was put to the ear before configureDeviceForCall.
							onSensorChanged(lastSensorEvent);
						}
					}
					updateOutputGainControlState();
					audioConfigured = true;
				});
			});
		}

		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor proximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		try {
			if (proximity != null) {
				proximityWakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "telegram-voip-prx");
				sm.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
			}
		} catch (Exception x) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.e("Error initializing proximity sensor", x);
			}
		}
	}

	private void fetchBluetoothDeviceName() {
		if (fetchingBluetoothDeviceName) {
			return;
		}
		try {
			currentBluetoothDeviceName = null;
			fetchingBluetoothDeviceName = true;
			BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, serviceListener, BluetoothProfile.HEADSET);
		} catch (Throwable e) {
			FileLog.e(e);
		}
	}

	private SensorEvent lastSensorEvent;

	@SuppressLint("NewApi")
	@Override
	public void onSensorChanged(SensorEvent event) {
		lastSensorEvent = event;
		if (unmutedByHold || remoteVideoState == Instance.VIDEO_STATE_ACTIVE || videoState[CAPTURE_DEVICE_CAMERA] == Instance.VIDEO_STATE_ACTIVE) {
			return;
		}
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
			VoipAudioManager vam = VoipAudioManager.get();
			if (audioRouteToSet != AUDIO_ROUTE_EARPIECE || isHeadsetPlugged || vam.isSpeakerphoneOn() || (isBluetoothHeadsetConnected() && am.isBluetoothScoOn())) {
				return;
			}
			boolean newIsNear = event.values[0] < Math.min(event.sensor.getMaximumRange(), 3);
			checkIsNear(newIsNear);
			NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.nearEarEvent, newIsNear);
		}
	}

	private void checkIsNear() {
		if (remoteVideoState == Instance.VIDEO_STATE_ACTIVE || videoState[CAPTURE_DEVICE_CAMERA] == Instance.VIDEO_STATE_ACTIVE) {
			checkIsNear(false);
		}
	}

	private void checkIsNear(boolean newIsNear) {
		if (newIsNear != isProximityNear) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("proximity " + newIsNear);
			}
			isProximityNear = newIsNear;
			try {
				if (isProximityNear) {
					proximityWakelock.acquire();
				} else {
					proximityWakelock.release(1); // this is non-public API before L
				}
			} catch (Exception x) {
				FileLog.e(x);
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public boolean isBluetoothHeadsetConnected() {
		if (USE_CONNECTION_SERVICE && systemCallConnection != null && systemCallConnection.getCallAudioState() != null) {
			return (systemCallConnection.getCallAudioState().getSupportedRouteMask() & CallAudioState.ROUTE_BLUETOOTH) != 0;
		}
		return isBtHeadsetConnected;
	}

	public void onAudioFocusChange(int focusChange) {
		if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			hasAudioFocus = true;
		} else {
			hasAudioFocus = false;
		}
	}

	private void updateBluetoothHeadsetState(boolean connected) {
		if (connected == isBtHeadsetConnected) {
			return;
		}
		if (BuildVars.LOGS_ENABLED) {
			FileLog.d("updateBluetoothHeadsetState: " + connected);
		}
		isBtHeadsetConnected = connected;
		final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (connected && !isRinging() && currentState != 0) {
			if (bluetoothScoActive) {
				if (BuildVars.LOGS_ENABLED) {
					FileLog.d("SCO already active, setting audio routing");
				}
				if (!hasRtmpStream()) {
					am.setSpeakerphoneOn(false);
					am.setBluetoothScoOn(true);
				}
			} else {
				if (BuildVars.LOGS_ENABLED) {
					FileLog.d("startBluetoothSco");
				}
				if (!hasRtmpStream()) {
					needSwitchToBluetoothAfterScoActivates = true;
					AndroidUtilities.runOnUIThread(() -> {
						try {
							am.startBluetoothSco();
						} catch (Throwable ignore) {

						}
					}, 500);
				}
			}
		} else {
			bluetoothScoActive = false;
			bluetoothScoConnecting = false;

			am.setBluetoothScoOn(false);
		}
		for (StateListener l : stateListeners) {
			l.onAudioSettingsChanged();
		}
	}

	public String getLastError() {
		return lastError;
	}

	public int getCallState() {
		return currentState;
	}

	public TLRPC.InputPeer getGroupCallPeer() {
		return groupCallPeer;
	}

	private void updateNetworkType() {
		if (tgVoip[CAPTURE_DEVICE_CAMERA] != null) {
			if (tgVoip[CAPTURE_DEVICE_CAMERA].isGroup()) {

			} else {
				tgVoip[CAPTURE_DEVICE_CAMERA].setNetworkType(getNetworkType());
			}
		} else {
			lastNetInfo = getActiveNetworkInfo();
		}
	}

	private int getNetworkType() {
		final NetworkInfo info = lastNetInfo = getActiveNetworkInfo();
		int type = Instance.NET_TYPE_UNKNOWN;
		if (info != null) {
			switch (info.getType()) {
				case ConnectivityManager.TYPE_MOBILE:
					switch (info.getSubtype()) {
						case TelephonyManager.NETWORK_TYPE_GPRS:
							type = Instance.NET_TYPE_GPRS;
							break;
						case TelephonyManager.NETWORK_TYPE_EDGE:
						case TelephonyManager.NETWORK_TYPE_1xRTT:
							type = Instance.NET_TYPE_EDGE;
							break;
						case TelephonyManager.NETWORK_TYPE_UMTS:
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
							type = Instance.NET_TYPE_3G;
							break;
						case TelephonyManager.NETWORK_TYPE_HSDPA:
						case TelephonyManager.NETWORK_TYPE_HSPA:
						case TelephonyManager.NETWORK_TYPE_HSPAP:
						case TelephonyManager.NETWORK_TYPE_HSUPA:
						case TelephonyManager.NETWORK_TYPE_EVDO_A:
						case TelephonyManager.NETWORK_TYPE_EVDO_B:
							type = Instance.NET_TYPE_HSPA;
							break;
						case TelephonyManager.NETWORK_TYPE_LTE:
							type = Instance.NET_TYPE_LTE;
							break;
						default:
							type = Instance.NET_TYPE_OTHER_MOBILE;
							break;
					}
					break;
				case ConnectivityManager.TYPE_WIFI:
					type = Instance.NET_TYPE_WIFI;
					break;
				case ConnectivityManager.TYPE_ETHERNET:
					type = Instance.NET_TYPE_ETHERNET;
					break;
			}
		}
		return type;
	}

	private NetworkInfo getActiveNetworkInfo() {
		return ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	}

	private void callFailed() {
		callFailed(tgVoip[CAPTURE_DEVICE_CAMERA] != null ? tgVoip[CAPTURE_DEVICE_CAMERA].getLastError() : Instance.ERROR_UNKNOWN);
	}

	public static Bitmap getRoundAvatarBitmap(Context context, int currentAccount, TLObject userOrChat) {
		Bitmap bitmap = null;
		try {
			if (userOrChat instanceof TLRPC.User) {
				TLRPC.User user = (TLRPC.User) userOrChat;
				if (user.photo != null && user.photo.photo_small != null) {
					BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, null, "50_50");
					if (img != null) {
						bitmap = img.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
					} else {
						try {
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inMutable = true;
							bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(currentAccount).getPathToAttach(user.photo.photo_small, true).toString(), opts);
						} catch (Throwable e) {
							FileLog.e(e);
						}
					}
				}
			} else {
				TLRPC.Chat chat = (TLRPC.Chat) userOrChat;
				if (chat != null && chat.photo != null && chat.photo.photo_small != null) {
					BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, null, "50_50");
					if (img != null) {
						bitmap = img.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
					} else {
						try {
							BitmapFactory.Options opts = new BitmapFactory.Options();
							opts.inMutable = true;
							bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(currentAccount).getPathToAttach(chat.photo.photo_small, true).toString(), opts);
						} catch (Throwable e) {
							FileLog.e(e);
						}
					}
				}
			}
		} catch (Throwable e) {
			FileLog.e(e);
		}
		if (bitmap == null) {
			Theme.createDialogsResources(context);
			AvatarDrawable placeholder;
			if (userOrChat instanceof TLRPC.User) {
				placeholder = new AvatarDrawable((TLRPC.User) userOrChat);
			} else {
				placeholder = new AvatarDrawable((TLRPC.Chat) userOrChat);
			}
			bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42), AndroidUtilities.dp(42), Bitmap.Config.ARGB_8888);
			placeholder.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
			placeholder.draw(new Canvas(bitmap));
		}

		Canvas canvas = new Canvas(bitmap);
		Path circlePath = new Path();
		circlePath.addCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, Path.Direction.CW);
		circlePath.toggleInverseFillType();
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		canvas.drawPath(circlePath, paint);
		return bitmap;
	}

	private void showIncomingNotification(String name, TLObject userOrChat, boolean video, int additionalMemberCount) {
		Intent intent = new Intent(this, LaunchActivity.class);
		intent.setAction("voip");

		Notification.Builder builder = new Notification.Builder(this)
				.setContentTitle(video ? LocaleController.getString(R.string.VoipInVideoCallBranding) : LocaleController.getString(R.string.VoipInCallBranding))
				.setSmallIcon(R.drawable.call)
				.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			SharedPreferences nprefs = MessagesController.getGlobalNotificationsSettings();
			int chanIndex = nprefs.getInt("calls_notification_channel", 0);
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationChannel oldChannel = nm.getNotificationChannel("incoming_calls2" + chanIndex);
			if (oldChannel != null) {
				nm.deleteNotificationChannel(oldChannel.getId());
			}
            oldChannel = nm.getNotificationChannel("incoming_calls3" + chanIndex);
            if (oldChannel != null) {
                nm.deleteNotificationChannel(oldChannel.getId());
            }
			NotificationChannel existingChannel = nm.getNotificationChannel("incoming_calls4" + chanIndex);
			boolean needCreate = true;
			if (existingChannel != null) {
				if (existingChannel.getImportance() < NotificationManager.IMPORTANCE_HIGH || existingChannel.getSound() != null) {
					if (BuildVars.LOGS_ENABLED) {
						FileLog.d("User messed up the notification channel; deleting it and creating a proper one");
					}
					nm.deleteNotificationChannel("incoming_calls4" + chanIndex);
					chanIndex++;
					nprefs.edit().putInt("calls_notification_channel", chanIndex).commit();
				} else {
					needCreate = false;
				}
			}
			if (needCreate) {
				AudioAttributes attrs = new AudioAttributes.Builder()
						.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.setLegacyStreamType(AudioManager.STREAM_RING)
						.setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
						.build();
				NotificationChannel chan = new NotificationChannel("incoming_calls4" + chanIndex, LocaleController.getString(R.string.IncomingCallsSystemSetting), NotificationManager.IMPORTANCE_HIGH);
                try {
                    chan.setSound(null, attrs);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                chan.setDescription(LocaleController.getString(R.string.IncomingCallsSystemSettingDescription));
				chan.enableVibration(false);
				chan.enableLights(false);
				chan.setBypassDnd(true);
				try {
					nm.createNotificationChannel(chan);
				} catch (Exception e) {
					FileLog.e(e);
					this.stopSelf();
					return;
				}
			}
			builder.setChannelId("incoming_calls4" + chanIndex);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.setSound(null);
		}
		Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
		endIntent.setAction(getPackageName() + ".DECLINE_CALL");
		endIntent.putExtra("call_id", getCallID());
		CharSequence endTitle = LocaleController.getString(R.string.VoipDeclineCall);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
			endTitle = new SpannableString(endTitle);
			((SpannableString) endTitle).setSpan(new ForegroundColorSpan(0xFFF44336), 0, endTitle.length(), 0);
		}
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, 0, endIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
		Intent answerIntent = new Intent(this, VoIPActionsReceiver.class);
		answerIntent.setAction(getPackageName() + ".ANSWER_CALL");
		answerIntent.putExtra("call_id", getCallID());
		CharSequence answerTitle = LocaleController.getString(R.string.VoipAnswerCall);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
			answerTitle = new SpannableString(answerTitle);
			((SpannableString) answerTitle).setSpan(new ForegroundColorSpan(0xFF00AA00), 0, answerTitle.length(), 0);
		}
		PendingIntent answerPendingIntent = PendingIntent.getBroadcast(this, 0, answerIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setPriority(Notification.PRIORITY_MAX);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			builder.setShowWhen(false);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.setColor(0xff2ca5e0);
			builder.setVibrate(new long[0]);
			builder.setCategory(Notification.CATEGORY_CALL);
			builder.setFullScreenIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE), true);
			if (userOrChat instanceof TLRPC.User) {
				TLRPC.User user = (TLRPC.User) userOrChat;
				if (!TextUtils.isEmpty(user.phone)) {
					builder.addPerson("tel:" + user.phone);
				}
			}
		}
		Notification incomingNotification;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			Bitmap avatar = getRoundAvatarBitmap(this, currentAccount, userOrChat);
			String personName = ContactsController.formatName(userOrChat);
			if (TextUtils.isEmpty(personName)) {
				//java.lang.IllegalArgumentException: person must have a non-empty a name
				personName = "___";
			}
			Person person = new Person.Builder()
					.setName(personName)
					.setIcon(Icon.createWithAdaptiveBitmap(avatar)).build();
			Notification.CallStyle notificationStyle = Notification.CallStyle.forIncomingCall(person, endPendingIntent, answerPendingIntent);

			builder.setStyle(notificationStyle);
			incomingNotification = builder.build();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
			builder.addAction(R.drawable.ic_call_end_white_24dp, endTitle, endPendingIntent);
			builder.addAction(R.drawable.call, answerTitle, answerPendingIntent);
			builder.setContentText(name);

			RemoteViews customView = new RemoteViews(getPackageName(), LocaleController.isRTL ? R.layout.call_notification_rtl : R.layout.call_notification);
			customView.setTextViewText(R.id.name, name);
			customView.setViewVisibility(R.id.subtitle, View.GONE);
			if (UserConfig.getActivatedAccountsCount() > 1) {
				TLRPC.User self = UserConfig.getInstance(currentAccount).getCurrentUser();
				customView.setTextViewText(R.id.title, video ? LocaleController.formatString("VoipInVideoCallBrandingWithName", R.string.VoipInVideoCallBrandingWithName, ContactsController.formatName(self.first_name, self.last_name)) : LocaleController.formatString("VoipInCallBrandingWithName", R.string.VoipInCallBrandingWithName, ContactsController.formatName(self.first_name, self.last_name)));
			} else {
				customView.setTextViewText(R.id.title, video ? LocaleController.getString(R.string.VoipInVideoCallBranding) : LocaleController.getString(R.string.VoipInCallBranding));
			}
			Bitmap avatar = getRoundAvatarBitmap(this, currentAccount, userOrChat);
			customView.setTextViewText(R.id.answer_text, LocaleController.getString(R.string.VoipAnswerCall));
			customView.setTextViewText(R.id.decline_text, LocaleController.getString(R.string.VoipDeclineCall));
			customView.setImageViewBitmap(R.id.photo, avatar);
			customView.setOnClickPendingIntent(R.id.answer_btn, answerPendingIntent);
			customView.setOnClickPendingIntent(R.id.decline_btn, endPendingIntent);
			builder.setLargeIcon(avatar);

			incomingNotification = builder.getNotification();
			incomingNotification.headsUpContentView = incomingNotification.bigContentView = customView;
		} else {
			builder.setContentText(name);
			builder.addAction(R.drawable.ic_call_end_white_24dp, endTitle, endPendingIntent);
			builder.addAction(R.drawable.call, answerTitle, answerPendingIntent);
			incomingNotification = builder.getNotification();
		}
		foregroundStarted = true;
		if (Build.VERSION.SDK_INT >= 33) {
			startForeground(foregroundId = ID_INCOMING_CALL_NOTIFICATION, foregroundNotification = incomingNotification, lastForegroundType = getCurrentForegroundType());
		} else {
			startForeground(foregroundId = ID_INCOMING_CALL_NOTIFICATION, foregroundNotification = incomingNotification);
		}
		startRingtoneAndVibration();
	}

	private boolean foregroundStarted;
	private int foregroundId;
	private Notification foregroundNotification;
	private int lastForegroundType;
	private boolean gotMediaProjection;

	private int getCurrentForegroundType() {
		return getCurrentForegroundType(this, gotMediaProjection);
	}
	private static int getCurrentForegroundType(ContextWrapper context, boolean gotMediaProjection) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			return (
				ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA |
				ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE |
				ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION |
				ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
			);
		}
		int type = 0;
		if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
			type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
		}
		if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
			type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
		}
		if (gotMediaProjection) {
			type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION;
		}
		type |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
		return type;
	}

	public void updateCurrentForegroundType() {
		if (lastForegroundType != getCurrentForegroundType() && foregroundStarted) {
			stopForeground(true);
			if (Build.VERSION.SDK_INT >= 33) {
				startForeground(foregroundId, foregroundNotification, lastForegroundType = getCurrentForegroundType());
			} else {
				startForeground(foregroundId, foregroundNotification);
			}
		}
	}

	private void callFailed(String error) {
		if (privateCall != null) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("Discarding failed call");
			}
			final TL_phone.discardCall req = new TL_phone.discardCall();
			req.peer = new TLRPC.TL_inputPhoneCall();
			req.peer.access_hash = privateCall.access_hash;
			req.peer.id = privateCall.id;
			req.duration = (int) (getCallDuration() / 1000);
			req.connection_id = tgVoip[CAPTURE_DEVICE_CAMERA] != null ? tgVoip[CAPTURE_DEVICE_CAMERA].getPreferredRelayId() : 0;
			req.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
			FileLog.e("discardCall " + req.reason);
			ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error1) -> {
				if (error1 != null) {
					if (BuildVars.LOGS_ENABLED) {
						FileLog.e("error on phone.discardCall: " + error1);
					}
				} else {
					if (BuildVars.LOGS_ENABLED) {
						FileLog.d("phone.discardCall " + response);
					}
				}
			});
		}
		try {
			throw new Exception("Call " + getCallID() + " failed with error: " + error);
		} catch (Exception x) {
			FileLog.e(x);
		}
		lastError = error;
		AndroidUtilities.runOnUIThread(() -> dispatchStateChanged(STATE_FAILED));
		if (TextUtils.equals(error, Instance.ERROR_LOCALIZED) && soundPool != null) {
			playingSound = true;
			Utilities.globalQueue.postRunnable(() -> soundPool.play(spFailedID, 1, 1, 0, 0, 1));
			AndroidUtilities.runOnUIThread(afterSoundRunnable, 1000);
		}
		if (USE_CONNECTION_SERVICE && systemCallConnection != null) {
			systemCallConnection.setDisconnected(new DisconnectCause(DisconnectCause.ERROR));
			systemCallConnection.destroy();
			systemCallConnection = null;
		}
		stopSelf();
	}

	void callFailedFromConnectionService() {
		if (isOutgoing) {
			callFailed(Instance.ERROR_CONNECTION_SERVICE);
		} else {
			hangUp();
		}
	}

	@Override
	public void onConnectionStateChanged(int newState, boolean inTransition) {
		AndroidUtilities.runOnUIThread(() -> {
			if (convertingVoip != null) {
				return;
			}
			if (newState == STATE_ESTABLISHED) {
				if (callStartTime == 0) {
					callStartTime = SystemClock.elapsedRealtime();
				}
				//peerCapabilities = tgVoip.getPeerCapabilities();
			}
			if (newState == STATE_FAILED) {
				callFailed();
				return;
			}
			if (newState == STATE_ESTABLISHED) {
				if (connectingSoundRunnable != null) {
					AndroidUtilities.cancelRunOnUIThread(connectingSoundRunnable);
					connectingSoundRunnable = null;
				}
				Utilities.globalQueue.postRunnable(() -> {
					if (spPlayId != 0) {
						soundPool.stop(spPlayId);
						spPlayId = 0;
					}
				});
				if (groupCall == null && !wasEstablished) {
					wasEstablished = true;
					if (!isProximityNear && !privateCall.video) {
						try {
							LaunchActivity.getLastFragment().getFragmentView().performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
						} catch (Exception ignore) {}
					}
					AndroidUtilities.runOnUIThread(new Runnable() {
						@Override
						public void run() {
							if (tgVoip[CAPTURE_DEVICE_CAMERA] != null) {
								StatsController.getInstance(currentAccount).incrementTotalCallsTime(getStatsNetworkType(), 5);
								AndroidUtilities.runOnUIThread(this, 5000);
							}
						}
					}, 5000);
					if (isOutgoing) {
						StatsController.getInstance(currentAccount).incrementSentItemsCount(getStatsNetworkType(), StatsController.TYPE_CALLS, 1);
					} else {
						StatsController.getInstance(currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), StatsController.TYPE_CALLS, 1);
					}
				}
			}
			if (newState == STATE_RECONNECTING && !isCallEnded) {
				Utilities.globalQueue.postRunnable(() -> {
					if (spPlayId != 0) {
						soundPool.stop(spPlayId);
					}
					spPlayId = soundPool.play(groupCall != null ? spVoiceChatConnecting : spConnectingId, 1, 1, 0, -1, 1);
				});
			}
			dispatchStateChanged(newState);
		});
	}

	public void playStartRecordSound() {
		Utilities.globalQueue.postRunnable(() -> soundPool.play(spStartRecordId, 0.5f, 0.5f, 0, 0, 1));
	}

	public void playAllowTalkSound() {
		Utilities.globalQueue.postRunnable(() -> soundPool.play(spAllowTalkId, 0.5f, 0.5f, 0, 0, 1));
	}

	@Override
	public void onSignalBarCountChanged(int newCount) {
		AndroidUtilities.runOnUIThread(() -> {
			signalBarCount = newCount;
			for (int a = 0; a < stateListeners.size(); a++) {
				StateListener l = stateListeners.get(a);
				l.onSignalBarsCountChanged(newCount);
			}
		});
	}

	public boolean isBluetoothOn() {
		final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		return am.isBluetoothScoOn();
	}

	public boolean isBluetoothWillOn() {
		return needSwitchToBluetoothAfterScoActivates;
	}

	public boolean isHeadsetPlugged() {
		return isHeadsetPlugged;
	}

	private void callEnded() {
		if (BuildVars.LOGS_ENABLED) {
			FileLog.d("Call " + getCallID() + " ended");
		}
		isCallEnded = true;
		if (groupCall != null && (!playedConnectedSound || onDestroyRunnable != null)) {
			needPlayEndSound = false;
		}
		AndroidUtilities.runOnUIThread(() -> dispatchStateChanged(STATE_ENDED));
		int delay = 700;
		Utilities.globalQueue.postRunnable(() -> {
			if (spPlayId != 0) {
				soundPool.stop(spPlayId);
				spPlayId = 0;
			}
		});

		if (connectingSoundRunnable != null) {
			AndroidUtilities.cancelRunOnUIThread(connectingSoundRunnable);
			connectingSoundRunnable = null;
		}
		if (needPlayEndSound) {
			playingSound = true;
			if (groupCall == null) {
				Utilities.globalQueue.postRunnable(() -> soundPool.play(spEndId, 1, 1, 0, 0, 1));
			} else {
				Utilities.globalQueue.postRunnable(() -> soundPool.play(spVoiceChatEndId, 1.0f, 1.0f, 0, 0, 1), 100);
				delay = 500;
			}
			AndroidUtilities.runOnUIThread(afterSoundRunnable, delay);
		}
		if (timeoutRunnable != null) {
			AndroidUtilities.cancelRunOnUIThread(timeoutRunnable);
			timeoutRunnable = null;
		}
		endConnectionServiceCall(needPlayEndSound ? delay : 0);
		stopSelf();
	}

	private void endConnectionServiceCall(long delay) {
		if (USE_CONNECTION_SERVICE) {
			Runnable r = () -> {
				if (systemCallConnection != null) {
					switch (callDiscardReason) {
						case DISCARD_REASON_HANGUP:
							systemCallConnection.setDisconnected(new DisconnectCause(isOutgoing ? DisconnectCause.LOCAL : DisconnectCause.REJECTED));
							break;
						case DISCARD_REASON_DISCONNECT:
							systemCallConnection.setDisconnected(new DisconnectCause(DisconnectCause.ERROR));
							break;
						case DISCARD_REASON_LINE_BUSY:
							systemCallConnection.setDisconnected(new DisconnectCause(DisconnectCause.BUSY));
							break;
						case DISCARD_REASON_MISSED:
							systemCallConnection.setDisconnected(new DisconnectCause(isOutgoing ? DisconnectCause.CANCELED : DisconnectCause.MISSED));
							break;
						default:
							systemCallConnection.setDisconnected(new DisconnectCause(DisconnectCause.REMOTE));
							break;
					}
					systemCallConnection.destroy();
					systemCallConnection = null;
				}
			};
			if (delay > 0) {
				AndroidUtilities.runOnUIThread(r, delay);
			} else {
				r.run();
			}
		}
	}

	public boolean isOutgoing() {
		return isOutgoing;
	}

	public void handleNotificationAction(Intent intent) {
		if ((getPackageName() + ".END_CALL").equals(intent.getAction())) {
			stopForeground(true);
			hangUp();
		} else if ((getPackageName() + ".DECLINE_CALL").equals(intent.getAction())) {
			stopForeground(true);
			declineIncomingCall(DISCARD_REASON_LINE_BUSY, null);
		} else if ((getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
			acceptIncomingCallFromNotification();
		}
	}

	private void acceptIncomingCallFromNotification() {
		showNotification();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || privateCall.video && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
			try {
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
				PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, VoIPPermissionActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ONE_SHOT).send();
			} catch (Exception x) {
				if (BuildVars.LOGS_ENABLED) {
					FileLog.e("Error starting permission activity", x);
				}
			}
			return;
		}
		acceptIncomingCall();
		try {
			PendingIntent.getActivity(VoIPService.this, 0, new Intent(VoIPService.this, getUIActivityClass()).setAction("voip"), PendingIntent.FLAG_MUTABLE).send();
		} catch (Exception x) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.e("Error starting incall activity", x);
			}
		}
	}

	public void updateOutputGainControlState() {
		if (hasRtmpStream()) {
			return;
		}
//		if (tgVoip[CAPTURE_DEVICE_CAMERA] != null) {
//			if (!USE_CONNECTION_SERVICE) {
//				final AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
//				boolean isSpeakerPhoneOn = VoipAudioManager.get().isSpeakerphoneOn();
//				tgVoip[CAPTURE_DEVICE_CAMERA].setAudioOutputGainControlEnabled(hasEarpiece() && !isSpeakerPhoneOn && !am.isBluetoothScoOn() && !isHeadsetPlugged);
//				tgVoip[CAPTURE_DEVICE_CAMERA].setEchoCancellationStrength(isHeadsetPlugged || (hasEarpiece() && !isSpeakerPhoneOn && !am.isBluetoothScoOn() && !isHeadsetPlugged) ? 0 : 1);
//			} else {
//				final boolean isEarpiece = systemCallConnection.getCallAudioState().getRoute() == CallAudioState.ROUTE_EARPIECE;
//				tgVoip[CAPTURE_DEVICE_CAMERA].setAudioOutputGainControlEnabled(isEarpiece);
//				tgVoip[CAPTURE_DEVICE_CAMERA].setEchoCancellationStrength(isEarpiece ? 0 : 1);
//			}
//		}
	}

	public int getAccount() {
		return currentAccount;
	}

	@Override
	public void didReceivedNotification(int id, int account, Object... args) {
		if (id == NotificationCenter.appDidLogout) {
			callEnded();
		}
	}

	public static boolean isAnyKindOfCallActive() {
		if (VoIPService.getSharedInstance() != null) {
			return VoIPService.getSharedInstance().getCallState() != VoIPService.STATE_WAITING_INCOMING;
		}
		return false;
	}

	private boolean isFinished() {
		return currentState == STATE_ENDED || currentState == STATE_FAILED;
	}

	public int getRemoteAudioState() {
		return remoteAudioState;
	}

	public int getRemoteVideoState() {
		return remoteVideoState;
	}

	@TargetApi(Build.VERSION_CODES.O)
	private PhoneAccountHandle addAccountToTelecomManager() {
		TelecomManager tm = (TelecomManager) getSystemService(TELECOM_SERVICE);
		TLRPC.User self = UserConfig.getInstance(currentAccount).getCurrentUser();
		PhoneAccountHandle handle = new PhoneAccountHandle(new ComponentName(this, TelegramConnectionService.class), "" + self.id);
		PhoneAccount account = new PhoneAccount.Builder(handle, ContactsController.formatName(self.first_name, self.last_name))
				.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
				.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_dr))
				.setHighlightColor(0xff2ca5e0)
				.addSupportedUriScheme("sip")
				.build();
		tm.registerPhoneAccount(account);
		return handle;
	}

	private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
			return false;
		}
		// some non-Google devices don't implement the ConnectionService API correctly so, sadly,
		// we'll have to whitelist only a handful of known-compatible devices for now
		return false;/*"angler".equals(Build.PRODUCT)            // Nexus 6P
				|| "bullhead".equals(Build.PRODUCT)        // Nexus 5X
				|| "sailfish".equals(Build.PRODUCT)        // Pixel
				|| "marlin".equals(Build.PRODUCT)        // Pixel XL
				|| "walleye".equals(Build.PRODUCT)        // Pixel 2
				|| "taimen".equals(Build.PRODUCT)        // Pixel 2 XL
				|| "blueline".equals(Build.PRODUCT)        // Pixel 3
				|| "crosshatch".equals(Build.PRODUCT)    // Pixel 3 XL
				|| MessagesController.getGlobalMainSettings().getBoolean("dbg_force_connection_service", false);*/
	}

	public interface StateListener {
		default void onStateChanged(int state) {

		}

		default void onSignalBarsCountChanged(int count) {

		}

		default void onAudioSettingsChanged() {

		}

		default void onMediaStateUpdated(int audioState, int videoState) {

		}

		default void onCameraSwitch(boolean isFrontFace) {

		}

		default void onCameraFirstFrameAvailable() {

		}

		default void onVideoAvailableChange(boolean isAvailable) {

		}

		default void onScreenOnChange(boolean screenOn) {

		}
	}

	public class CallConnection extends Connection {
		public CallConnection() {
			setConnectionProperties(PROPERTY_SELF_MANAGED);
			setAudioModeIsVoip(true);
		}

		@Override
		public void onCallAudioStateChanged(CallAudioState state) {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("ConnectionService call audio state changed: " + state);
			}
			for (StateListener l : stateListeners) {
				l.onAudioSettingsChanged();
			}
		}

		@Override
		public void onDisconnect() {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("ConnectionService onDisconnect");
			}
			setDisconnected(new DisconnectCause(DisconnectCause.LOCAL));
			destroy();
			systemCallConnection = null;
			hangUp();
		}

		@Override
		public void onAnswer() {
			acceptIncomingCallFromNotification();
		}

		@Override
		public void onReject() {
			needPlayEndSound = false;
			declineIncomingCall(DISCARD_REASON_HANGUP, null);
		}

		@Override
		public void onShowIncomingCallUi() {
			startRinging();
		}

		@Override
		public void onStateChanged(int state) {
			super.onStateChanged(state);
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("ConnectionService onStateChanged " + stateToString(state));
			}
			if (state == Connection.STATE_ACTIVE) {
				ContactsController.getInstance(currentAccount).deleteConnectionServiceContact();
				didDeleteConnectionServiceContact = true;
			}
		}

		@Override
		public void onCallEvent(String event, Bundle extras) {
			super.onCallEvent(event, extras);
			if (BuildVars.LOGS_ENABLED)
				FileLog.d("ConnectionService onCallEvent " + event);
		}

		//undocumented API
		public void onSilence() {
			if (BuildVars.LOGS_ENABLED) {
				FileLog.d("onSlience");
			}
			stopRinging();
		}
	}

	public void processMessageUpdate(MessageObject messageObject) {
		if (messageObject == null) return;
		if (messageObject.messageOwner == null) return;
		if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionConferenceCall) {
			final long dialogId = messageObject.getDialogId();
			final int msg_id = messageObject.getId();
			final TLRPC.TL_messageActionConferenceCall action = (TLRPC.TL_messageActionConferenceCall) messageObject.messageOwner.action;

			if (groupCall != null) {
				final ChatObject.Call.InvitedUser invited = groupCall.invitedUsersMessageIds.get(dialogId);
				if (invited != null && invited.msg_id == msg_id) {
					final boolean calling = !action.missed && !action.active;
					if (invited.isCalling() != calling) {
						invited.calling = calling;
						groupCall.invitedUsersMessageIds.put(dialogId, invited);
						NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.groupCallUpdated, 0L, groupCall.call.id, false);
					}
				}
			}
		}
	}

	public static class SharedUIParams {
		public boolean tapToVideoTooltipWasShowed;
		public boolean cameraAlertWasShowed;
		public boolean wasVideoCall;
	}
}
