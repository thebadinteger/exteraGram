data.reset(); // reset input to b2
					data.mark(header.getFrameSize() + 2); // rest of frame (size - 2) + next header
					/*
					 * read frame data
					 */
					byte[] frameBytes = new byte[header.getFrameSize()];
					frameBytes[0] = (byte) 0xFF;
					frameBytes[1] = (byte) b1;
					try {
						data.readFully(frameBytes, 2, frameBytes.length - 2); // may throw EOFException
					} catch (EOFException e) {
						break;
					}

					MP3Frame frame = new MP3Frame(header, frameBytes);
					/*
					 * read next header  
					 */
					if (!frame.isChecksumError()) {
						int nextB0 = stopCondition.stopRead(data) ? -1 : data.read();
						int nextB1 = stopCondition.stopRead(data) ? -1 : data.read();
						if (nextB0 == -1 || nextB1 == -1) {
							return frame;
						}
						if (nextB0 == 0xFF && (nextB1 & 0xFE) == (b1 & 0xFE)) { // quick check: nextB1 must match b1's version & layer
							int nextB2 = stopCondition.stopRead(data) ? -1 : data.read();
							int nextB3 = stopCondition.stopRead(data) ? -1 : data.read();
							if (nextB2 == -1 || nextB3 == -1) {
								return frame;
							}
							if (new MP3Frame.Header(nextB1, nextB2, nextB3).isCompatible(header)) {
								data.reset(); // reset input to b2
								data.skipFully(frameBytes.length - 2); // skip to end of frame
								return frame;
							}
						}
					}
				}

				/*
				 * seems to be a false sync...
				 */
				data.reset(); // reset input to b2
			}

			/*
			 * read next byte
			 */
			b0 = b1;
			b1 = stopCondition.stopRead(data) ? -1 : data.read();
		}
		return null;
	}

	MP3Frame readNextFrame(MP3Input data, StopReadCondition stopCondition, MP3Frame previousFrame) throws IOException, MP3Exception {
		MP3Frame.Header previousHeader = previousFrame.getHeader();
		data.mark(4);
		int b0 = stopCondition.stopRead(data) ? -1 : data.read();
		int b1 = stopCondition.stopRead(data) ? -1 : data.read();
		if (b0 == -1 || b1 == -1) {
			return null;
		}
		if (b0 == 0xFF && (b1 & 0xE0) == 0xE0) { // first 11 bits should be 1
			int b2 = stopCondition.stopRead(data) ? -1 : data.read();
			int b3 = stopCondition.stopRead(data) ? -1 : data.read();
			if (b2 == -1 || b3 == -1) {
				return null;
			}
			MP3Frame.Header nextHeader = null;
			try {
				nextHeader = new MP3Frame.Header(b1, b2, b3);
			} catch (MP3Exception e) {
				// not a valid frame header
				data.exceptionsCount++;
				if (data.exceptionsCount > 5) {
					throw e;
				}
			}
			if (nextHeader != null && nextHeader.isCompatible(previousHeader)) {
				byte[] frameBytes = new byte[nextHeader.getFrameSize()];
				frameBytes[0] = (byte) b0;
				frameBytes[1] = (byte) b1;
				frameBytes[2] = (byte) b2;
				frameBytes[3] = (byte) b3;
				try {
					data.readFully(frameBytes, 4, frameBytes.length - 4);
				} catch (EOFException e) {
					return null;
				}
				return new MP3Frame(nextHeader, frameBytes);
			}
		}
		data.reset();
		return null;
	}

	long calculateDuration(MP3Input data, long totalLength, StopReadCondition stopCondition) throws IOException, MP3Exception {
		MP3Frame frame = readFirstFrame(data, stopCondition);
		if (frame != null) {
			// check for Xing header
			int numberOfFrames = frame.getNumberOfFrames();
			if (numberOfFrames > 0) { // from Xing/VBRI header
				return frame.getHeader().getTotalDuration(numberOfFrames * frame.getSize());
			} else { // scan file
				numberOfFrames = 1;

				long firstFramePosition = data.getPosition() - frame.getSize();
				long frameSizeSum = frame.getSize();

				int firstFrameBitrate = frame.getHeader().getBitrate();
				long bitrateSum = firstFrameBitrate;
				boolean vbr = false;
				int cbrThreshold = 10000 / frame.getHeader().getDuration(); // assume CBR after 10 seconds

				while (true) {
					if (numberOfFrames == cbrThreshold && !vbr && totalLength > 0) {
						return frame.getHeader().getTotalDuration(totalLength - firstFramePosition);
					}
					if ((frame = readNextFrame(data, stopCondition, frame)) == null) {
						break;
					}
					int bitrate = frame.getHeader().getBitrate();
					if (bitrate != firstFrameBitrate) {
						vbr = true;
					}
					bitrateSum += bitrate;
					frameSizeSum += frame.getSize();
					numberOfFrames++;
				}
				return 1000L * frameSizeSum * numberOfFrames * 8 / bitrateSum;
			}
		} else {
			throw new MP3Exception("No audio frame");
		}
	}
}
