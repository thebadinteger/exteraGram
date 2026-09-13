package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;

public class AvcConfigurationBox extends AbstractBox {
    public AvcConfigurationBox() {
        super("avcC");
    }

    public void setAvcLevelIndication(int level) {}
    public void setAvcProfileIndication(int profile) {}
    public void setBitDepthLumaMinus8(int depth) {}
    public void setBitDepthChromaMinus8(int depth) {}
    public void setChromaFormat(int format) {}
    public void setConfigurationVersion(int version) {}
    public void setLengthSizeMinusOne(int size) {}
    public void setProfileCompatibility(int comp) {}
    public void setSequenceParameterSets(java.util.List<byte[]> sps) {}
    public void setPictureParameterSets(java.util.List<byte[]> pps) {}

    @Override protected long getContentSize() { return 0; }
    @Override protected void getContent(ByteBuffer byteBuffer) {}
    @Override protected void _parseDetails(ByteBuffer content) {}
}
