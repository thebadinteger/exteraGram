public class RangeInputStream extends PositionInputStream {
	private final long endPosition;
	
	public RangeInputStream(InputStream delegate, long position, long length) throws IOException {
		super(delegate, position);
		this.endPosition = position + length;
	}
	
	public long getRemainingLength() {
		return endPosition - getPosition();
	}
	
	@Override
	public int read() throws IOException {
		if (getPosition() == endPosition) {
			return -1;
		}
		return super.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (getPosition() + len > endPosition) {
			len  = (int)(endPosition - getPosition());
			if (len == 0) {
				return -1;
			}
		}
		return super.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException {
		if (getPosition() + n > endPosition) {
			n  = (int)(endPosition - getPosition());
		}
		return super.skip(n);
	}
}
