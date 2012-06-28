/*
 * Javolution - Java(TM) Solution for Real-Time and Embedded Systems
 * Copyright (C) 2005 - Javolution (http://javolution.org/)
 * All rights reserved.
 * 
 * Permission to use, copy, modify, and distribute this software is
 * freely granted, provided that this notice is preserved.
 */
package java.nio;

/**
 * Clean-room implementation of ByteBuffer to support 
 * <code>javolution.util.Struct</code> when <code>java.nio</code> is
 * not available.
 */
public final class ByteBuffer extends Buffer {

    private ByteOrder _order = ByteOrder.BIG_ENDIAN;

    private final byte[] _bytes;
	private final int offset;
	private final int length;

    private ByteBuffer(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}

    private ByteBuffer(byte[] bytes, int offset, int length) {
        super(-1, 0, length, length);
        _bytes = bytes;
		this.offset = offset;
		this.length = length;
    }

    public static ByteBuffer allocateDirect(int capacity) {
        return new ByteBuffer(new byte[capacity]);
    }

    public static ByteBuffer allocate(int capacity) {
        return new ByteBuffer(new byte[capacity]);
    }

    final public static ByteBuffer wrap(byte[] array, int offset, int length) {
        return new ByteBuffer(array, offset, length);
    }

    final public static ByteBuffer wrap(byte[] array) {
        return new ByteBuffer(array);
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            dst[i] = get();
        }

        return this;
    }

    public ByteBuffer get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public ByteBuffer put(ByteBuffer src) {
        if (src.remaining() > 0) {
            byte[] toPut = new byte[src.remaining()];
            src.get(toPut);
            src.put(toPut);
        }

        return this;
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        for (int i = offset; i < offset + length; i++)
            put(src[i]);

        return this;
    }

    public final ByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    public final boolean hasArray() {
        return true;
    }

    public final byte[] array() {
        return _bytes;
    }

    public final int arrayOffset() {
        return offset;
    }

    public final ByteOrder order() {
        return _order;
    }

    public final ByteBuffer order(ByteOrder endian) {
        _order = endian;
        return this;
    }

    public byte get() {
        return _bytes[_position++];
    }

    public ByteBuffer put(byte b) {
        _bytes[_position++] = b;
        return this;
    }

    public byte get(int index) {
        return _bytes[index];
    }

    public ByteBuffer put(int index, byte b) {
        _bytes[index] = b;
        return this;
    }

    public boolean isDirect() {
        return false;
    }

    public char getChar() {
        return getChar(_position++);
    }

	private int increasePosition(int size) {
		int p = _position;
		_position += size;
		return p;
	}

    public ByteBuffer putChar(char value) {
        putChar(_position++, value);
        return this;
    }

    public char getChar(int index) {
        return (char) getShort(index);
    }

    public ByteBuffer putChar(int index, char value) {
        return putShort(index, (short) value);
    }

    public short getShort() {
        return getShort(increasePosition(2));
    }

    public ByteBuffer putShort(short value) {
        return putShort(increasePosition(2), value);
    }

    public short getShort(int index) {
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((_bytes[index] & 0xff) + (_bytes[index + 1] << 8));
        } else {
            return (short) ((_bytes[index] << 8) + (_bytes[index + 1] & 0xff));
        }
    }

    public ByteBuffer putShort(int index, short value) {
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[index] = (byte) value;
            _bytes[++index] = (byte) (value >> 8);
        } else {
            _bytes[index] = (byte) (value >> 8);
            _bytes[++index] = (byte) value;
        }
        return this;
    }

    public int getInt() {
        return getInt(increasePosition(4));
    }

    public ByteBuffer putInt(int value) {
        return putInt(increasePosition(4), value);
    }

    public int getInt(int index) {
		int result;
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (_bytes[index] & 0xff) + ((_bytes[index + 1] & 0xff) << 8)
                    + ((_bytes[index + 2] & 0xff) << 16)
                    + ((_bytes[index + 3] & 0xff) << 24);
        } else {
            return (_bytes[index] << 24) + ((_bytes[index + 1] & 0xff) << 16)
                    + ((_bytes[index + 2] & 0xff) << 8)
                    + (_bytes[index + 3] & 0xff);
        }
    }

    public ByteBuffer putInt(int index, int value) {
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[index] = (byte) value;
            _bytes[++index] = (byte) (value >> 8);
            _bytes[++index] = (byte) (value >> 16);
            _bytes[++index] = (byte) (value >> 24);
        } else {
            _bytes[index] = (byte) (value >> 24);
            _bytes[++index] = (byte) (value >> 16);
            _bytes[++index] = (byte) (value >> 8);
            _bytes[++index] = (byte) value;
        }
        return this;
    }

    public long getLong() {
        return getLong(increasePosition(8));
    }

    public ByteBuffer putLong(long value) {
        return putLong(increasePosition(8), value);
    }

    public long getLong(int index) {
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            return (_bytes[index] & 0xff)
				    + ((_bytes[index + 1] & 0xff) << 8)
                    + ((_bytes[index + 2] & 0xff) << 16)
                    + ((_bytes[index + 3] & 0xffL) << 24)
                    + ((_bytes[index + 4] & 0xffL) << 32)
                    + ((_bytes[index + 5] & 0xffL) << 40)
                    + ((_bytes[index + 6] & 0xffL) << 48)
                    + (((long) _bytes[index + 7]) << 56);
        } else {
            return (((long) _bytes[index]) << 56)
                    + ((_bytes[index + 1] & 0xffL) << 48)
                    + ((_bytes[index + 2] & 0xffL) << 40)
                    + ((_bytes[index + 3] & 0xffL) << 32)
                    + ((_bytes[index + 4] & 0xffL) << 24)
                    + ((_bytes[index + 5] & 0xff) << 16)
                    + ((_bytes[index + 6] & 0xff) << 8)
                    + (_bytes[index + 7] & 0xffL);
        }
    }

    public ByteBuffer putLong(int index, long value) {
        if (_order == ByteOrder.LITTLE_ENDIAN) {
            _bytes[index] = (byte) value;
            _bytes[++index] = (byte) (value >> 8);
            _bytes[++index] = (byte) (value >> 16);
            _bytes[++index] = (byte) (value >> 24);
            _bytes[++index] = (byte) (value >> 32);
            _bytes[++index] = (byte) (value >> 40);
            _bytes[++index] = (byte) (value >> 48);
            _bytes[++index] = (byte) (value >> 56);
        } else {
            _bytes[index] = (byte) (value >> 56);
            _bytes[++index] = (byte) (value >> 48);
            _bytes[++index] = (byte) (value >> 40);
            _bytes[++index] = (byte) (value >> 32);
            _bytes[++index] = (byte) (value >> 24);
            _bytes[++index] = (byte) (value >> 16);
            _bytes[++index] = (byte) (value >> 8);
            _bytes[++index] = (byte) value;
        }
        return this;
    }

    /*@JVM-1.1+@

    public float getFloat() {
        return getFloat(_position++);
    }

    public ByteBuffer putFloat(float value) {
        return putFloat(_position++, value);
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    public ByteBuffer putFloat(int index, float value) {
        return putInt(index, Float.floatToIntBits(value));
    }

    public double getDouble() {
        return getDouble(_position++);
    }

    public ByteBuffer putDouble(double value) {
        return putDouble(_position++, value);
    }

    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public ByteBuffer putDouble(int index, double value) {
        return putLong(index, Double.doubleToLongBits(value));
    }
    
    /**/
}
