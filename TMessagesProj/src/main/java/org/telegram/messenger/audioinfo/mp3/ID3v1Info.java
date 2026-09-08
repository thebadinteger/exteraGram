if (bytes[125] == 0 && bytes[126] != 0) {
                version = "1.1";
                track = (short) (bytes[126] & 0xFF);
            }
        }
    }

    byte[] readBytes(InputStream input, int len) throws IOException {
        int total = 0;
        byte[] bytes = new byte[len];
        while (total < len) {
            int current = input.read(bytes, total, len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
        return bytes;
    }

    String extractString(byte[] bytes, int offset, int length) {
        try {
            String text = new String(bytes, offset, length, "ISO-8859-1");
            int zeroIndex = text.indexOf(0);
            return zeroIndex < 0 ? text : text.substring(0, zeroIndex);
        } catch (Exception e) {
            return "";
        }
    }
}
