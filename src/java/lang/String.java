/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The {@code String} class represents character strings. All
 * string literals in Java programs, such as {@code "abc"}, are
 * implemented as instances of this class.
 * <p>
 * Strings are constant; their values cannot be changed after they
 * are created. String buffers support mutable strings.
 * Because String objects are immutable they can be shared. For example:
 * <blockquote><pre>
 *     String str = "abc";
 * </pre></blockquote><p>
 * is equivalent to:
 * <blockquote><pre>
 *     char data[] = {'a', 'b', 'c'};
 *     String str = new String(data);
 * </pre></blockquote><p>
 * Here are some more examples of how strings can be used:
 * <blockquote><pre>
 *     System.out.println("abc");
 *     String cde = "cde";
 *     System.out.println("abc" + cde);
 *     String c = "abc".substring(2,3);
 *     String d = cde.substring(1, 2);
 * </pre></blockquote>
 * <p>
 * The class {@code String} includes methods for examining
 * individual characters of the sequence, for comparing strings, for
 * searching strings, for extracting substrings, and for creating a
 * copy of a string with all characters translated to uppercase or to
 * lowercase. Case mapping is based on the Unicode Standard version
 * specified by the {@link java.lang.Character Character} class.
 * <p>
 * The Java language provides special support for the string
 * concatenation operator (&nbsp;+&nbsp;), and for conversion of
 * other objects to strings. String concatenation is implemented
 * through the {@code StringBuilder}(or {@code StringBuffer})
 * class and its {@code append} method.
 * String conversions are implemented through the method
 * {@code toString}, defined by {@code Object} and
 * inherited by all classes in Java. For additional information on
 * string concatenation and conversion, see Gosling, Joy, and Steele,
 * <i>The Java Language Specification</i>.
 *
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * <p>A {@code String} represents a string in the UTF-16 format
 * in which <em>supplementary characters</em> are represented by <em>surrogate
 * pairs</em> (see the section <a href="Character.html#unicode">Unicode
 * Character Representations</a> in the {@code Character} class for
 * more information).
 * Index values refer to {@code char} code units, so a supplementary
 * character uses two positions in a {@code String}.
 * <p>The {@code String} class provides methods for dealing with
 * Unicode code points (i.e., characters), in addition to those for
 * dealing with Unicode code units (i.e., {@code char} values).
 *
 * @author Lee Boynton
 * @author Arthur van Hoff
 * @author Martin Buchholz
 * @author Ulf Zibis
 * @see java.lang.Object#toString()
 * @see java.lang.StringBuffer
 * @see java.lang.StringBuilder
 * @see java.nio.charset.Charset
 * @since JDK1.0
 */

/**
 * 1、不可变类(类值一旦被初始化，就不能再被改变了，如果被修改，将会是新的类【也可以说是新的对象】)
 *  ①String为不可变类的原因：
 *   a、String被final修饰，说明String类绝不可能被继承了，也就是说任何对String的操作方
 *      法，都不会被继承覆写。
 *   b、String 中保存数据的是一个char的数组 value。这个value也是被final修饰的，也就
 *     是说value一旦被赋值，内存地址是绝对无法修改的，而且value的权限是private的，外
 *     部绝对访问不到，String也没有开放出可以对value进行赋值的方法，所以说value一旦产
 *     生，内存地址就根本无法被修改。
 * 2、String类表示字符串。
 *   Java程序中的所有字符串文字（例如“ abc”）都实现为此类的实例。
 * 3、字符串是常量； 它们的值创建后无法更改。 字符串缓冲区支持可变字符串。
 *   由于String对象是不可变的，因此可以共享它们。
 * 4、String类实现了三个接口，分别为 Serializable、Comparable 和 CharSequence。
 *   其中 Serializable 接口表明其可以序列化。
 */
public final class String
        implements java.io.Serializable, Comparable<String>, CharSequence {
    /** The value is used for character storage. */
    /*该值用于字符存储  ---  字符串的底层存储结构*/
    private final char value[];

    /**
     * Cache the hash code for the string
     */
    /*字符串对象的哈希值，默认值为0*/
    private int hash; // Default to 0

    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * Class String is special cased within the Serialization Stream Protocol.
     * <p>
     * A String instance is written into an ObjectOutputStream according to
     * <a href="{@docRoot}/../platform/serialization/spec/output.html">
     * Object Serialization Specification, Section 6.2, "Stream Elements"</a>
     */
    private static final ObjectStreamField[] serialPersistentFields =
            new ObjectStreamField[0];

    /**
     * Initializes a newly created {@code String} object so that it represents
     * an empty character sequence.  Note that use of this constructor is
     * unnecessary since Strings are immutable.
     */
    /**
     * 没有参数的构造方法直接将空字符串的 value进行赋值
     * 注意:由于字符串是不可变的，因此不需要使用此构造函数。
     */
    public String() {
        this.value = "".value;
    }

    /**
     * Initializes a newly created {@code String} object so that it represents
     * the same sequence of characters as the argument; in other words, the
     * newly created string is a copy of the argument string. Unless an
     * explicit copy of {@code original} is needed, use of this constructor is
     * unnecessary since Strings are immutable.
     *
     * @param  original
     *         A {@code String}
     */
    /**
     * 1、传入 String 对象的构造方法则将该对象对应的 value 、hash 进行赋值
     * 2、初始化一个新创建的String对象，以便它表示与参数相同的字符序列；
     *    换句话说，新创建的字符串是参数字符串的副本。
     *    除非需要original的显式副本，否则不需要使用此构造函数，因为字符串是不可变的。
     *
     * @param original 一个String对象
     */
    public String(String original) {
        this.value = original.value;
        this.hash = original.hash;
    }

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     */
    /**
     * 1、分配一个新的String对象，以便它表示字符数组参数中当前包含的字符序列。
     *    字符数组的内容被复制； 字符数组的后续修改不会影响新创建的字符串。
     *
     * @param value  字符串的初始值
     */
    public String(char value[]) {
        //拷贝字符数组
        this.value = Arrays.copyOf(value, value.length);
    }

    /**
     * Allocates a new {@code String} that contains characters from a subarray
     * of the character array argument. The {@code offset} argument is the
     * index of the first character of the subarray and the {@code count}
     * argument specifies the length of the subarray. The contents of the
     * subarray are copied; subsequent modification of the character array does
     * not affect the newly created string.
     *
     * @param  value
     *         Array that is the source of characters
     *
     * @param  offset
     *         The initial offset
     *
     * @param  count
     *         The length
     *
     * @throws IndexOutOfBoundsException
     *          If the {@code offset} and {@code count} arguments index
     *          characters outside the bounds of the {@code value} array
     */

    /**
     * 1、分配一个新的字符串，其中包含来自字符数组参数的子数组的字符。
     *    offset参数（开始下标）是子数组第一个字符的下标，
     *    count 参数指定子数组的长度。
     *    子数组的内容被复制：字符数组的后续修改不会影响新创建的字符串。
     *
     * @param value  作为源的字符数组
     * @param offset 初始偏移量
     * @param count  新字符串长度
     */
    public String(char value[], int offset, int count) {

        //若起始下标为负数，则抛出数组下标越界异常
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }

        if (count <= 0) {
            //若新字符串的长度为负数，则抛出数组下标越界异常
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            //若新字符串的长度为0，直接创建""的字符串。
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        // 偏移量或新长度可能接近-1 >>> 1。(也就是防止新的字符串的长度超过2147483647，char []value的长度是2147483647)
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        //拷贝产生新的字符串
        this.value = Arrays.copyOfRange(value, offset, offset + count);
    }

    /**
     * Allocates a new {@code String} that contains characters from a subarray
     * of the <a href="Character.html#unicode">Unicode code point</a> array
     * argument.  The {@code offset} argument is the index of the first code
     * point of the subarray and the {@code count} argument specifies the
     * length of the subarray.  The contents of the subarray are converted to
     * {@code char}s; subsequent modification of the {@code int} array does not
     * affect the newly created string.
     *
     * @param codePoints Array that is the source of Unicode code points
     * @param offset     The initial offset
     * @param count      The length
     * @throws IllegalArgumentException  If any invalid Unicode code point is found in {@code
     *                                   codePoints}
     * @throws IndexOutOfBoundsException If the {@code offset} and {@code count} arguments index
     *                                   characters outside the bounds of the {@code codePoints} array
     * @since 1.5
     */
    public String(int[] codePoints, int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= codePoints.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > codePoints.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }

        final int end = offset + count;

        // Pass 1: Compute precise size of char[]
        int n = count;
        for (int i = offset; i < end; i++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                continue;
            else if (Character.isValidCodePoint(c))
                n++;
            else throw new IllegalArgumentException(Integer.toString(c));
        }

        // Pass 2: Allocate and fill in char[]
        final char[] v = new char[n];

        for (int i = offset, j = 0; i < end; i++, j++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c))
                v[j] = (char) c;
            else
                Character.toSurrogates(c, v, j++);
        }

        this.value = v;
    }

    /**
     * Allocates a new {@code String} constructed from a subarray of an array
     * of 8-bit integer values.
     *
     * <p> The {@code offset} argument is the index of the first byte of the
     * subarray, and the {@code count} argument specifies the length of the
     * subarray.
     *
     * <p> Each {@code byte} in the subarray is converted to a {@code char} as
     * specified in the method above.
     *
     * @param ascii  The bytes to be converted to characters
     * @param hibyte The top 8 bits of each 16-bit Unicode code unit
     * @param offset The initial offset
     * @param count  The length
     * @throws IndexOutOfBoundsException If the {@code offset} or {@code count} argument is invalid
     * @see #String(byte[], int)
     * @see #String(byte[], int, int, java.lang.String)
     * @see #String(byte[], int, int, java.nio.charset.Charset)
     * @see #String(byte[], int, int)
     * @see #String(byte[], java.lang.String)
     * @see #String(byte[], java.nio.charset.Charset)
     * @see #String(byte[])
     * @deprecated This method does not properly convert bytes into characters.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link
     * java.nio.charset.Charset}, charset name, or that use the platform's
     * default charset.
     */
    @Deprecated
    public String(byte ascii[], int hibyte, int offset, int count) {
        checkBounds(ascii, offset, count);
        char value[] = new char[count];

        if (hibyte == 0) {
            for (int i = count; i-- > 0; ) {
                value[i] = (char) (ascii[i + offset] & 0xff);
            }
        } else {
            hibyte <<= 8;
            for (int i = count; i-- > 0; ) {
                value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
            }
        }
        this.value = value;
    }

    /**
     * Allocates a new {@code String} containing characters constructed from
     * an array of 8-bit integer values. Each character <i>c</i>in the
     * resulting string is constructed from the corresponding component
     * <i>b</i> in the byte array such that:
     *
     * <blockquote><pre>
     *     <b><i>c</i></b> == (char)(((hibyte &amp; 0xff) &lt;&lt; 8)
     *                         | (<b><i>b</i></b> &amp; 0xff))
     * </pre></blockquote>
     *
     * @param ascii  The bytes to be converted to characters
     * @param hibyte The top 8 bits of each 16-bit Unicode code unit
     * @see #String(byte[], int, int, java.lang.String)
     * @see #String(byte[], int, int, java.nio.charset.Charset)
     * @see #String(byte[], int, int)
     * @see #String(byte[], java.lang.String)
     * @see #String(byte[], java.nio.charset.Charset)
     * @see #String(byte[])
     * @deprecated This method does not properly convert bytes into
     * characters.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link
     * java.nio.charset.Charset}, charset name, or that use the platform's
     * default charset.
     */
    @Deprecated
    public String(byte ascii[], int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }

    /* Common private utility method used to bounds check the byte array
     * and requested offset & length values used by the String(byte[],..)
     * constructors.
     */

    /**
     * 常用的私有方法：用于字节数组的边界检查以及String（byte []，..）构造函数使用的请求的偏移量和长度值。
     *
     * @param bytes  字节数组
     * @param offset 第一个字节的下标(偏移量)
     * @param length 字节数
     */
    private static void checkBounds(byte[] bytes, int offset, int length) {
        //字节数小于0，则抛异常
        if (length < 0)
            throw new StringIndexOutOfBoundsException(length);
        //第一个字节的下标小于0，则抛异常
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        //偏移量+字节数>字节数组的长度，则抛异常
        if (offset > bytes.length - length)
            throw new StringIndexOutOfBoundsException(offset + length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified charset.  The length of the new {@code String}
     * is a function of the charset, and hence may not be equal to the length
     * of the subarray.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  offset
     *         The index of the first byte to decode
     *
     * @param  length
     *         The number of bytes to decode

     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @throws UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @throws IndexOutOfBoundsException
     *          If the {@code offset} and {@code length} arguments index
     *          characters outside the bounds of the {@code bytes} array
     *
     * @since JDK1.1
     */
    /**
     * 1、通过使用指定的字符集解码指定的字节子数组，构造一个新的String对象。
     *    新的String的长度是字符集的函数，因此可能不等于子数组的长度。
     * 2、未指定给定字符集中给定字节无效时此构造函数的行为。
     *    当需要对解码过程进行更多控制时，应使用{@link java.nio.charset.CharsetDecoder}类。
     *
     * @param bytes       要解码为字符的字节数组
     * @param offset      要解码的第一个字节的下标
     * @param length      要解码的字节数
     * @param charsetName 受支持的java.nio.charset.Charset charset的名称
     * @throws UnsupportedEncodingException 如果offset和length自变量下标的字符超出 bytes数组的边界
     */
    public String(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        //字符集为空，则抛异常
        if (charsetName == null)
            throw new NullPointerException("charsetName");
        //检查参数(边界方面)
        checkBounds(bytes, offset, length);
        //给定字节数组，指定指定编码进行解码成数组
        this.value = StringCoding.decode(charsetName, bytes, offset, length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the subarray.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param bytes   The bytes to be decoded into characters
     * @param offset  The index of the first byte to decode
     * @param length  The number of bytes to decode
     * @param charset The {@linkplain java.nio.charset.Charset charset} to be used to
     *                decode the {@code bytes}
     * @throws IndexOutOfBoundsException If the {@code offset} and {@code length} arguments index
     *                                   characters outside the bounds of the {@code bytes} array
     * @since 1.6
     */
    public String(byte bytes[], int offset, int length, Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset");
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(charset, bytes, offset, length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the specified {@linkplain java.nio.charset.Charset charset}.  The
     * length of the new {@code String} is a function of the charset, and hence
     * may not be equal to the length of the byte array.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param bytes       The bytes to be decoded into characters
     * @param charsetName The name of a supported {@linkplain java.nio.charset.Charset
     *                    charset}
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @since JDK1.1
     */
    public String(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, charsetName);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param bytes   The bytes to be decoded into characters
     * @param charset The {@linkplain java.nio.charset.Charset charset} to be used to
     *                decode the {@code bytes}
     * @since 1.6
     */
    public String(byte bytes[], Charset charset) {
        this(bytes, 0, bytes.length, charset);
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the platform's default charset.  The length of the new
     * {@code String} is a function of the charset, and hence may not be equal
     * to the length of the subarray.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param bytes  The bytes to be decoded into characters
     * @param offset The index of the first byte to decode
     * @param length The number of bytes to decode
     * @throws IndexOutOfBoundsException If the {@code offset} and the {@code length} arguments index
     *                                   characters outside the bounds of the {@code bytes} array
     * @since JDK1.1
     */
    public String(byte bytes[], int offset, int length) {
        checkBounds(bytes, offset, length);
        this.value = StringCoding.decode(bytes, offset, length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the platform's default charset.  The length of the new {@code
     * String} is a function of the charset, and hence may not be equal to the
     * length of the byte array.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param bytes The bytes to be decoded into characters
     * @since JDK1.1
     */
    public String(byte bytes[]) {
        this(bytes, 0, bytes.length);
    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string buffer argument. The contents of the
     * string buffer are copied; subsequent modification of the string buffer
     * does not affect the newly created string.
     *
     * @param buffer A {@code StringBuffer}
     */
    public String(StringBuffer buffer) {
        synchronized (buffer) {
            this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
        }
    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string builder argument. The contents of the
     * string builder are copied; subsequent modification of the string builder
     * does not affect the newly created string.
     *
     * <p> This constructor is provided to ease migration to {@code
     * StringBuilder}. Obtaining a string from a string builder via the {@code
     * toString} method is likely to run faster and is generally preferred.
     *
     * @param builder A {@code StringBuilder}
     * @since 1.5
     */
    public String(StringBuilder builder) {
        this.value = Arrays.copyOf(builder.getValue(), builder.length());
    }

    /*
     * Package private constructor which shares value array for speed.
     * this constructor is always expected to be called with share==true.
     * a separate constructor is needed because we already have a public
     * String(char[]) constructor that makes a copy of the given char[].
     */

    /**
     * 1、包私有构造函数，它共享值数组。
     *   这个构造函数总是希望使用share == true调用它。
     *   需要一个单独的构造函数，因为我们已经有一个公共String（char []）构造函数，它复制给定char []。
     *
     * @param value
     * @param share
     */
    String(char[] value, boolean share) {
        //断言 共享：“不共享不支持”；
        // assert share : "unshared not supported";
        this.value = value;
    }

    /**
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return the length of the sequence of characters represented by this
     *          object.
     */
    /**
     * 1、返回此字符串的长度。
     *   长度等于字符串中<a href="Character.html#unicode"> Unicode代码单元</a>的数量。(字符串的长度就是字节数组的长度)
     *
     * @return 此对象表示的字符序列的长度
     */
    public int length() {
        return value.length;
    }

    /**
     * Returns {@code true} if, and only if, {@link #length()} is {@code 0}.
     *
     * @return {@code true} if {@link #length()} is {@code 0}, otherwise
     * {@code false}
     *
     * @since 1.6
     */
    /**
     * 1、仅在#length()为0的情况下，返回true。  （通过判断char数组长度是否为0来判断字符串对象是否为空。）
     *
     * @return 如果#length()为0，则为true，否则为false
     */
    public boolean isEmpty() {
        return value.length == 0;
    }

    /**
     * Returns the {@code char} value at the
     * specified index. An index ranges from {@code 0} to
     * {@code length() - 1}. The first {@code char} value of the sequence
     * is at index {@code 0}, the next at index {@code 1},
     * and so on, as for array indexing.
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the {@code char} value.
     * @return the {@code char} value at the specified index of this string.
     *             The first {@code char} value is at index {@code 0}.
     * @exception IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     */
    /**
     * 1、返回指定下标处的char值。
     *   a、下标的范围是 0到length()-1。
     *   b、序列的第一个char值位于下标0，下一个位于下标1，依此类推，与数组下标一样。
     *
     * 2、如果下标指定的char值是<a href="Character.html#unicode">代理人</a>，则会返回代理人值。
     *
     * @param index char值的下标
     * @return 此字符串的指定下标处的char值。 第一个char值在索引0处
     * @throws IndexOutOfBoundsException 如果index参数为负或不小于此字符串的长度
     */
    public char charAt(int index) {
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        //从字符数组中获取
        return value[index];
    }

    /**
     * Returns the character (Unicode code point) at the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 0} to
     * {@link #length()}{@code  - 1}.
     *
     * <p> If the {@code char} value specified at the given index
     * is in the high-surrogate range, the following index is less
     * than the length of this {@code String}, and the
     * {@code char} value at the following index is in the
     * low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise,
     * the {@code char} value at the given index is returned.
     *
     * @param      index the index to the {@code char} values
     * @return the code point value of the character at the
     *             {@code index}
     * @exception IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     * @since 1.5
     */
    /**
     * 1、返回指定下标处的字符（Unicode值）。
     *    下标引用char值（Unicode代码单位），范围从0到{@link #length()}-1。
     * 2、如果在给定下标处指定的char值在高代理范围内，则以下下标小于此String的长度，
     *    并且在以下下标处的char值是在低代理范围内，则返回与此代理对相对应的补充代码点。
     *    否则，将返回给定下标处的char值。
     *
     * @param index
     * @return
     */
    public int codePointAt(int index) {
        //检测是否数组下面越界
        if ((index < 0) || (index >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointAtImpl(value, index, value.length);
    }

    /**
     * Returns the character (Unicode code point) before the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 1} to {@link
     * CharSequence#length() length}.
     *
     * <p> If the {@code char} value at {@code (index - 1)}
     * is in the low-surrogate range, {@code (index - 2)} is not
     * negative, and the {@code char} value at {@code (index -
     * 2)} is in the high-surrogate range, then the
     * supplementary code point value of the surrogate pair is
     * returned. If the {@code char} value at {@code index -
     * 1} is an unpaired low-surrogate or a high-surrogate, the
     * surrogate value is returned.
     *
     * @param     index the index following the code point that should be returned
     * @return the Unicode code point value before the given index.
     * @exception IndexOutOfBoundsException if the {@code index}
     *            argument is less than 1 or greater than the length
     *            of this string.
     * @since 1.5
     */
    /**
     * 1、用于返回指定下标值前一个字符的unicode值。
     *   实现与java.lang.String#codePointAt(int)类似
     *
     * @param index
     * @return
     */
    public int codePointBefore(int index) {
        //下标值减1
        int i = index - 1;
        if ((i < 0) || (i >= value.length)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return Character.codePointBeforeImpl(value, index, 0);
    }

    /**
     * Returns the number of Unicode code points in the specified text
     * range of this {@code String}. The text range begins at the
     * specified {@code beginIndex} and extends to the
     * {@code char} at index {@code endIndex - 1}. Thus the
     * length (in {@code char}s) of the text range is
     * {@code endIndex-beginIndex}. Unpaired surrogates within
     * the text range count as one code point each.
     *
     * @param beginIndex the index to the first {@code char} of
     * the text range.
     * @param endIndex the index after the last {@code char} of
     * the text range.
     * @return the number of Unicode code points in the specified text
     * range
     * @exception IndexOutOfBoundsException if the
     * {@code beginIndex} is negative, or {@code endIndex}
     * is larger than the length of this {@code String}, or
     * {@code beginIndex} is larger than {@code endIndex}.
     * @since 1.5
     */
    /**
     * 1、返回此String的指定文本范围内的字符个数。(指定下标范围内)
     *    文本范围从指定的beginIndex开始，并扩展到下标 endIndex-1的char。
     *    因此，文本范围的长度（以{@code char} s为单位）为 endIndex-beginIndex。
     *    文本范围内的不成对代理每个都计为一个代码点。
     *
     * @param beginIndex 文本范围的第一个char的下标。
     * @param endIndex   文本范围的最后一个char之后的下标。
     * @return 指定文本范围内的字符个数
     */
    public int codePointCount(int beginIndex, int endIndex) {
        //若开始下标小于0或者结束下标大于字符数组长度或者开始下标大于结束下标，则抛出异常
        if (beginIndex < 0 || endIndex > value.length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
    }

    /**
     * Returns the index within this {@code String} that is
     * offset from the given {@code index} by
     * {@code codePointOffset} code points. Unpaired surrogates
     * within the text range given by {@code index} and
     * {@code codePointOffset} count as one code point each.
     *
     * @param index the index to be offset
     * @param codePointOffset the offset in code points
     * @return the index within this {@code String}
     * @exception IndexOutOfBoundsException if {@code index}
     *   is negative or larger then the length of this
     *   {@code String}, or if {@code codePointOffset} is positive
     *   and the substring starting with {@code index} has fewer
     *   than {@code codePointOffset} code points,
     *   or if {@code codePointOffset} is negative and the substring
     *   before {@code index} has fewer than the absolute value
     *   of {@code codePointOffset} code points.
     * @since 1.5
     */
    /**
     * 1、返回此String中的下标，该下标与给定的index偏移codePointOffset个代码点。
     *    index和codePointOffset给出的文本范围内的不成对的替代项分别计为一个代码点。
     *
     * @param index           要偏移的下标
     * @param codePointOffset 代码点的偏移量
     * @return 此字符串中的下标
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        //校验下标是否越界
        if (index < 0 || index > value.length) {
            throw new IndexOutOfBoundsException();
        }

        return Character.offsetByCodePointsImpl(value, 0, value.length,
                index, codePointOffset);
    }

    /**
     * Copy characters from this string into dst starting at dstBegin.
     * This method doesn't perform any range checking.
     */
    /**
     * 1、从dstBegin开始，将字符串中的字符复制到dst中。 此方法不执行任何范围检查。
     *
     * @param dst      目标数组
     * @param dstBegin 目标数据中的起始位置
     */
    void getChars(char dst[], int dstBegin) {
        System.arraycopy(value, 0, dst, dstBegin, value.length);
    }

    /**
     * Copies characters from this string into the destination character
     * array.
     * <p>
     * The first character to be copied is at index {@code srcBegin};
     * the last character to be copied is at index {@code srcEnd-1}
     * (thus the total number of characters to be copied is
     * {@code srcEnd-srcBegin}). The characters are copied into the
     * subarray of {@code dst} starting at index {@code dstBegin}
     * and ending at index:
     * <blockquote><pre>
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param      srcBegin   index of the first character in the string
     *                        to copy.
     * @param      srcEnd     index after the last character in the string
     *                        to copy.
     * @param      dst        the destination array.
     * @param      dstBegin   the start offset in the destination array.
     * @exception IndexOutOfBoundsException If any of the following
     *            is true:
     *            <ul><li>{@code srcBegin} is negative.
     *            <li>{@code srcBegin} is greater than {@code srcEnd}
     *            <li>{@code srcEnd} is greater than the length of this
     *                string
     *            <li>{@code dstBegin} is negative
     *            <li>{@code dstBegin+(srcEnd-srcBegin)} is larger than
     *                {@code dst.length}</ul>
     */
    /**
     * 1、将字符串中的字符复制到目标字符数组中。
     * 2、要复制的第一个字符在下标srcBegin处；
     *    最后要复制的字符位于下标srcEnd-1（因此，要复制的字符总数为srcEnd-srcBegin）。
     *    字符被复制到dst的子数组中，从下标dstBegin开始，到下标处结束 = dstBegin +（srcEnd-srcBegin）-1
     *
     * @param srcBegin 要复制的字符串中第一个字符的下标
     * @param srcEnd   要复制的字符串中最后一个字符之后的下标
     * @param dst      目标数组
     * @param dstBegin 目标数组中的起始偏移量
     *                 <p>
     *                 如果以下任一情况为真： a、srcBegin为负。
     *                 b、srcBegin大于srcEnd
     *                 c、srcEnd大于此字符串的长度
     *                 d、dstBegin为负
     *                 e、dstBegin +（ srcEnd-srcBegin）大于dst.length
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        //srcBegin为负,则抛下标越界异常
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        //srcEnd大于此字符串的长度,则抛下标越界异常
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        //srcBegin大于srcEnd,则抛下标越界异常
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * Copies characters from this string into the destination byte array. Each
     * byte receives the 8 low-order bits of the corresponding character. The
     * eight high-order bits of each character are not copied and do not
     * participate in the transfer in any way.
     *
     * <p> The first character to be copied is at index {@code srcBegin}; the
     * last character to be copied is at index {@code srcEnd-1}.  The total
     * number of characters to be copied is {@code srcEnd-srcBegin}. The
     * characters, converted to bytes, are copied into the subarray of {@code
     * dst} starting at index {@code dstBegin} and ending at index:
     *
     * <blockquote><pre>
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param srcBegin Index of the first character in the string to copy
     * @param srcEnd   Index after the last character in the string to copy
     * @param dst      The destination array
     * @param dstBegin The start offset in the destination array
     * @throws IndexOutOfBoundsException If any of the following is true:
     *                                   <ul>
     *                                     <li> {@code srcBegin} is negative
     *                                     <li> {@code srcBegin} is greater than {@code srcEnd}
     *                                     <li> {@code srcEnd} is greater than the length of this String
     *                                     <li> {@code dstBegin} is negative
     *                                     <li> {@code dstBegin+(srcEnd-srcBegin)} is larger than {@code
     *                                          dst.length}
     *                                   </ul>
     * @deprecated This method does not properly convert characters into
     * bytes.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@link #getBytes()} method, which uses the platform's default charset.
     */
    @Deprecated
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        Objects.requireNonNull(dst);

        int j = dstBegin;
        int n = srcEnd;
        int i = srcBegin;
        char[] val = value;   /* avoid getfield opcode */

        while (i < n) {
            dst[j++] = (byte) val[i++];
        }
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the named
     * charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @return The resultant byte array
     *
     * @throws UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @since JDK1.1
     */
    /**
     * 1、使用命名的字符集将此字符串编码为字节序列，并将结果存储到新的字节数组中。
     * 注意：未指定无法在给定字符集中编码此字符串时此方法的行为。 如果需要对编码过程进行更多控制，则应使用{@link java.nio.charset.CharsetEncoder}类。
     *
     * @param charsetName 字符集名称
     * @return 字节数组
     * @throws UnsupportedEncodingException 如果不支持指定的字符集会抛出异常
     */
    public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException {
        if (charsetName == null) throw new NullPointerException();
        return StringCoding.encode(charsetName, value, 0, value.length);
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the given
     * {@linkplain java.nio.charset.Charset charset}, storing the result into a
     * new byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement byte array.  The
     * {@link java.nio.charset.CharsetEncoder} class should be used when more
     * control over the encoding process is required.
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset} to be used to encode
     *         the {@code String}
     *
     * @return The resultant byte array
     *
     * @since 1.6
     */
    /**
     * 1、使用给定的{@linkplain java.nio.charset.Charset charset}将此String编码为字节序列，并将结果存储到新的字节数组中。
     * 2、此方法始终使用此字符集的默认替换字节数组替换格式错误的输入和不可映射的字符序列。 如果需要对编码过程进行更多控制，则应使用{@link java.nio.charset.CharsetEncoder}类。
     *
     * @param charset {@linkplain java.nio.charset.Charset}用于对{@code String}进行编码
     * @return 返回字节数组
     */
    public byte[] getBytes(Charset charset) {
        if (charset == null) throw new NullPointerException();
        return StringCoding.encode(charset, value, 0, value.length);
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the
     * platform's default charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @return The resultant byte array
     *
     * @since JDK1.1
     */
    /**
     * 1、使用平台的默认字符集将此String编码为字节序列，并将结果存储到新的字节数组中。
     *   注意：未指定在默认字符集中无法编码此字符串时此方法的行为。
     *   如果需要对编码过程进行更多控制，则应使用{@link java.nio.charset.CharsetEncoder}类。
     *
     * @return 返回字节数组
     */
    public byte[] getBytes() {
        return StringCoding.encode(value, 0, value.length);
    }

    /**
     * Compares this string to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * String} object that represents the same sequence of characters as this
     * object.
     *
     * @param  anObject
     *         The object to compare this {@code String} against
     *
     * @return  {@code true} if the given object represents a {@code String}
     *          equivalent to this string, {@code false} otherwise
     *
     * @see  #compareTo(String)
     * @see  #equalsIgnoreCase(String)
     */
    /**
     * 1、将此字符串与指定对象进行比较。
     *   当且仅当参数不是null并且是一个String对象，并且表示与此对象相同的字符序列时，结果为true。
     *
     * @param anObject 与当前String对象进行比较的对象
     * @return 如果给定的对象表示与此字符串等效的String对象，则返回true，否则返回false
     */
    public boolean equals(Object anObject) {
        // 判断内存地址是否相同
        if (this == anObject) {
            return true;
        }
        //待比较的对象是否是 String类型的，如果不是 String，直接返回不相等
        if (anObject instanceof String) {
            //注意：String类是不可变类，所以入参只能是String类型的
            //当前对象的类型是String类，则强转入参为String类型
            String anotherString = (String) anObject;
            // 获取当前对象的char[]数组的长度
            int n = value.length;
            // 两个字符串的长度是否相等，不等则直接返回不相等
            if (n == anotherString.value.length) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = 0;
                //依次比较每个字符是否相等，若有一个不等，直接返回不相等
                while (n-- != 0) {
                    if (v1[i] != v2[i])
                        return false;
                    i++;
                }
                return true;
            }
        }
        //比较的对象不是String类或两个字符串的长度不相等的话，则直接返回false
        return false;
    }

    /**
     * Compares this string to the specified {@code StringBuffer}.  The result
     * is {@code true} if and only if this {@code String} represents the same
     * sequence of characters as the specified {@code StringBuffer}. This method
     * synchronizes on the {@code StringBuffer}.
     *
     * @param  sb
     *         The {@code StringBuffer} to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of characters as the specified {@code StringBuffer},
     *          {@code false} otherwise
     *
     * @since 1.4
     */
    /**
     * 1、将此字符串与指定的StringBuffer比较。
     *   当且仅当此String表示与指定的StringBuffer相同的字符序列时，结果为true。
     *   此方法在StringBuffer上同步。
     *
     * @param sb 将与此String比较的StringBuffer
     * @return 如果此{@code字符串}表示与指定的{@code StringBuffer}相同的字符序列，则为true，否则为false
     */
    public boolean contentEquals(StringBuffer sb) {
        return contentEquals((CharSequence) sb);
    }

    private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
        char v1[] = value;
        char v2[] = sb.getValue();
        int n = v1.length;
        //判断两个长度不相等,则返回 false
        if (n != sb.length()) {
            return false;
        }
        //一个一个字符进行比较，若有一个字符不相等，则返回false
        for (int i = 0; i < n; i++) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this string to the specified {@code CharSequence}.  The
     * result is {@code true} if and only if this {@code String} represents the
     * same sequence of char values as the specified sequence. Note that if the
     * {@code CharSequence} is a {@code StringBuffer} then the method
     * synchronizes on it.
     *
     * @param  cs
     *         The sequence to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of char values as the specified sequence, {@code
     *          false} otherwise
     *
     * @since 1.5
     */
    /**
     * 1、将此字符串与指定的CharSequence比较。
     *   当且仅当此String表示与指定序列相同的char值序列时，结果为true。
     *   请注意，如果CharSequence是StringBuffer，则该方法将在其上同步。
     *
     * @param cs 与此String比较的字符序列
     * @return 如果此字符串表示与指定序列相同的char值序列，则为true，否则为false
     */
    public boolean contentEquals(CharSequence cs) {
        // Argument is a StringBuffer, StringBuilder
        // 是否由 AbstractStringBuilder 实例化出来，是的话表示它为 StringBuilder 或 StringBuffer 对象。
        if (cs instanceof AbstractStringBuilder) {
            //如果为 StringBuffer 对象，说明它是线程安全的，需要做同步处理，调用nonSyncContentEquals方法。
            if (cs instanceof StringBuffer) {
                synchronized (cs) {
                    return nonSyncContentEquals((AbstractStringBuilder) cs);
                }
            } else {
                // 如果为 StringBuilder 对象，它是非线程安全的，直接调用nonSyncContentEquals方法。
                return nonSyncContentEquals((AbstractStringBuilder) cs);
            }
        }
        // Argument is a String
        // 如果为 String 对象，则调用equals方法比较
        if (cs instanceof String) {
            return equals(cs);
        }
        // Argument is a generic CharSequence
        // 接下来是属于 CharSequence 对象时的逻辑
        //当前字符串的字符数组
        char v1[] = value;
        //当前字符串的字符数组的长度
        int n = v1.length;
        //若字符数组长度不等于CharSequence对象的长度，则返回false
        if (n != cs.length()) {
            return false;
        }
        //for循环比较每一个字符，如果有一个字符不相同，则返回false
        for (int i = 0; i < n; i++) {
            if (v1[i] != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this {@code String} to another {@code String}, ignoring case
     * considerations.  Two strings are considered equal ignoring case if they
     * are of the same length and corresponding characters in the two strings
     * are equal ignoring case.
     *
     * <p> Two characters {@code c1} and {@code c2} are considered the same
     * ignoring case if at least one of the following is true:
     * <ul>
     *   <li> The two characters are the same (as compared by the
     *        {@code ==} operator)
     *   <li> Applying the method {@link
     *        java.lang.Character#toUpperCase(char)} to each character
     *        produces the same result
     *   <li> Applying the method {@link
     *        java.lang.Character#toLowerCase(char)} to each character
     *        produces the same result
     * </ul>
     *
     * @param  anotherString
     *         The {@code String} to compare this {@code String} against
     *
     * @return  {@code true} if the argument is not {@code null} and it
     *          represents an equivalent {@code String} ignoring case; {@code
     *          false} otherwise
     *
     * @see  #equals(Object)
     */
    /**
     * 1、用于对比字符串是否相等，而且是忽略大小写。（如果是自己与自己对比则为true，否则需要两者长度相等且regionMatches方法返回true才为true）
     * 2、将此String与另一个String进行比较，而忽略大小写考虑。
     *    如果两个字符串的长度相同，并且两个字符串中的对应字符相等，则忽略大小写。
     * 3、如果满足以下至少一个条件，则两个字符c1和c2被视为相同的忽略大小写：
     *   a、两个字符相同（与{@code = =}运算符）
     *   b、将方法{@link java.lang.Character＃toUpperCase（char）}应用于每个字符会产生相同的结果。
     *   c、应用方法{@link java.lang.Character＃toLowerCase（char） }对每个字符产生相同的结果。
     *
     * @param anotherString
     * @return
     */
    public boolean equalsIgnoreCase(String anotherString) {
        //如果是自己与自己对比则为true，否则字符串不为空并且两个字符串的字符数组的长度相等并且需要两者长度相等且regionMatches方法返回true才为true
        return (this == anotherString) ? true
                : (anotherString != null)
                && (anotherString.value.length == value.length)
                //忽略大小写从下标0开始比较当前字符串和anotherString字符串，比较长度为当前字符串的字符数组的长度
                && regionMatches(true, 0, anotherString, 0, value.length);
    }

    /**
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * {@code String} object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this {@code String} object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this {@code String} object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; {@code compareTo} returns {@code 0} exactly when
     * the {@link #equals(Object)} method would return {@code true}.
     * <p>
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, {@code compareTo} returns the
     * difference of the two character values at position {@code k} in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * {@code compareTo} returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param anotherString the {@code String} to be compared.
     * @return the value {@code 0} if the argument string is equal to
     * this string; a value less than {@code 0} if this string
     * is lexicographically less than the string argument; and a
     * value greater than {@code 0} if this string is
     * lexicographically greater than the string argument.
     */
    /**
     * 1、用于比较两个字符串
     *
     * 2、按字典顺序比较两个字符串。
     *   比较是基于字符串中每个字符的Unicode值。
     *   在字典上比较此String对象表示的字符序列与自变量字符串表示的字符序列。
     *   如果此String对象在字典上在参数字符串之前，则结果为负整数。
     *   如果此String对象在字典上跟随自变量字符串，则结果为正整数。
     *   如果字符串相等，则结果为零；
     *   否则，结果为零。
     *   compareTo恰好在#equals（Object）方法返回true时返回0。
     * 3、这是字典顺序的定义。
     *    如果两个字符串不同，那么它们要么在某个下标处具有不同的字符（这是两个字符串的有效下标），要么它们的长度不同，或者两者都有。
     *    如果它们在一个或多个下标位置具有不同的字符，则将<i> k </ i>设为最小的下标；
     *    然后，字符串<i> k </ i>处的字符具有较小的值，具体取决于使用；
     *    运算符，按字典顺序在另一个字符串之前。
     *    在这种情况下，compareTo返回两个字符串中位置k的两个字符值的差，即值：
     *    this.charAt(k) - anotherString.charAt(k)
     *    如果没有下标位置不同，则从字法上讲，较短的字符串在较长的字符串之前。
     *    在这种情况下，compareTo返回字符串长度的差值，即值：this.length()-anotherString.length()
     *
     * @param anotherString  进行比较的String
     * @return  如果参数字符串等于此字符串，则值为0；
     *          如果此字符串在字典上小于字符串参数，则小于0的值；
     *          如果该字符串在字典上大于字符串参数，则该值大于0。
     */
    public int compareTo(String anotherString) {
        //当前字符串的的字符数组长度
        int len1 = value.length;
        //入参字符串的字符数组长度
        int len2 = anotherString.value.length;
        //两个字符串中的字符数组的长度最小值
        int lim = Math.min(len1, len2);
        //当前字符串的的字符数组
        char v1[] = value;
        //入参字符串的字符数组
        char v2[] = anotherString.value;

        int k = 0;
        //循环比较两个字符串中小于两个字符串中的字符数组的长度最小值的字符
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            //比较字符是否箱等，不相等，则返回字符差值
            if (c1 != c2) {
                return c1 - c2;
            }
            //比较下标加1
            k++;
        }
        //若前面的都相等，则返回当前字符串的的字符数组长度-入参字符串的字符数组长度的值
        return len1 - len2;
    }

    /**
     * A Comparator that orders {@code String} objects as by
     * {@code compareToIgnoreCase}. This comparator is serializable.
     * <p>
     * Note that this Comparator does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>Collators</em> to allow
     * locale-sensitive ordering.
     *
     * @see     java.text.Collator#compare(String, String)
     * @since 1.2
     */
    /**
     * 1、用于排序的比较器
     * 2、、一个按 compareToIgnoreCase排序String对象的比较器。
     *     该比较器是可序列化的。
     * 3、请注意，该比较器不会考虑语言环境，并且会导致某些语言环境的排序不理想。
     *    java.text包提供了Collators，以允许对语言环境敏感的排序。
     *
     * @see java.text.Collator#compare(String, String)
     */
    public static final Comparator<String> CASE_INSENSITIVE_ORDER
            = new CaseInsensitiveComparator();

    /**
     * 1、该静态内部类提供排序的比较器，实现了Comparator接口的compare方法
     *    注意：这里比较字母的大写是否相等，又比较字母的小写是否相等。
     *    原因：Java为了针对Georgian（格鲁吉亚）字母表奇怪的大小写转换规则而专门又增加了一步判断，就是转换成小写再比较一次，Java的国际化真的做的好
     *    请看 @see java.lang.String#regionMatches(boolean, int, java.lang.String, int, int)
     */
    private static class CaseInsensitiveComparator
            implements Comparator<String>, java.io.Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability
        private static final long serialVersionUID = 8575799808933029326L;

        public int compare(String s1, String s2) {
            //获取两者的长度
            int n1 = s1.length();
            int n2 = s2.length();
            //获取两者中的最小长度
            int min = Math.min(n1, n2);
            //遍历两个字符串的前min位
            for (int i = 0; i < min; i++) {
                char c1 = s1.charAt(i);
                char c2 = s2.charAt(i);
                //若c1不等于c2字符的话
                if (c1 != c2) {
                    //将c1和c2先转换成大写字母进行比较
                    c1 = Character.toUpperCase(c1);
                    c2 = Character.toUpperCase(c2);
                    //如果两者的大写字母不相等，然后再转换成小写字母进行比较
                    if (c1 != c2) {
                        c1 = Character.toLowerCase(c1);
                        c2 = Character.toLowerCase(c2);
                        //若两个小写字母不相等，则直接返回这两个字符的相减值即可
                        if (c1 != c2) {
                            // No overflow because of numeric promotion
                            // 由于数字提升而没有溢出
                            return c1 - c2;
                        }
                    }
                }
            }
            //长度相减   1、若前面都相等，越长的越大
            return n1 - n2;
        }

        /** Replaces the de-serialized object. */
        /**
         * 用于替换反序列化时的对象
         */
        private Object readResolve() {
            return CASE_INSENSITIVE_ORDER;
        }
    }

    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling {@code compareTo} with normalized versions of the strings
     * where case differences have been eliminated by calling
     * {@code Character.toLowerCase(Character.toUpperCase(character))} on
     * each character.
     * <p>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param str the {@code String} to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * specified String is greater than, equal to, or less
     * than this String, ignoring case considerations.
     * @see java.text.Collator#compare(String, String)
     * @since 1.2
     */
    /**
     * 1、按字典顺序比较两个字符串，忽略大小写差异。
     *    此方法返回一个整数，该整数的符号是使用字符串的规范化版本调用compareTo的字符串，其中通过在每个字符上调用Character.toLowerCase(Character.toUpperCase(character))消除了大小写差异。
     * 2、请注意：此方法不会考虑语言环境，并且会导致某些语言环境的排序不理想。
     *    java.text包提供了collators，以允许对语言环境敏感的排序。
     *
     * @param str  要比较的字符串
     * @return 指定的String大于，等于或小于此String时，为负整数，零或正整数，而忽略大小写考虑。
     *         @see java.text.Collator＃compare（String，String）
     */
    public int compareToIgnoreCase(String str) {
        //实现通过CaseInsensitiveComparator内部类来实现
        return CASE_INSENSITIVE_ORDER.compare(this, str);
    }

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument other. The result is true if these substrings
     * represent identical character sequences. The substring of this
     * {@code String} object to be compared begins at index {@code toffset}
     * and has length {@code len}. The substring of other to be compared
     * begins at index {@code ooffset} and has length {@code len}. The
     * result is {@code false} if and only if at least one of the following
     * is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>There is some nonnegative integer <i>k</i> less than {@code len}
     * such that:
     * {@code this.charAt(toffset + }<i>k</i>{@code ) != other.charAt(ooffset + }
     * <i>k</i>{@code )}
     * </ul>
     *
     * @param toffset the starting offset of the subregion in this string.
     * @param other   the string argument.
     * @param ooffset the starting offset of the subregion in the string
     *                argument.
     * @param len     the number of characters to compare.
     * @return {@code true} if the specified subregion of this string
     * exactly matches the specified subregion of the string argument;
     * {@code false} otherwise.
     */
    /**
     * 1、测试两个字符串区域是否相等。
     *   将此String对象的子字符串与参数other的子字符串进行比较。
     *   如果这些子字符串表示相同的字符序列，则结果为true。
     *   要比较的此String对象的子字符串从下标toffset开始，长度为len。
     *   要比较的另一个的子串从下标ooffset开始，长度为len。
     *   当且仅当以下至少一项为真时，结果为false：
     *     a、toffset为负。
     *     b、ooffset为负。
     *     c、toffset + len大于此String对象的长度。
     *     d、ooffset + len大于另一个参数的长度。
     *     c、存在一些小于len的非负整数k，使得：this.charAt（toffset +  k ） ！= other.charAt（ooffset + k ）
     * @param toffset
     * @param other
     * @param ooffset
     * @param len
     * @return
     */
    public boolean regionMatches(int toffset, String other, int ooffset,
                                 int len) {
        char ta[] = value;
        int to = toffset;
        char pa[] = other.value;
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        //若toffset为负或ooffset为负或toffset + len大于此String对象的长度或ooffset + len大于另一个参数的长度，则返回false
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long) value.length - len)
                || (ooffset > (long) other.value.length - len)) {
            return false;
        }
        //遍历比较每个字符，若有一个不相等，则返回false
        while (len-- > 0) {
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument {@code other}. The result is {@code true} if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if {@code ignoreCase} is true. The substring of
     * this {@code String} object to be compared begins at index
     * {@code toffset} and has length {@code len}. The substring of
     * {@code other} to be compared begins at index {@code ooffset} and
     * has length {@code len}. The result is {@code false} if and only if
     * at least one of the following is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>{@code ignoreCase} is {@code false} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * this.charAt(toffset+k) != other.charAt(ooffset+k)
     * </pre></blockquote>
     * <li>{@code ignoreCase} is {@code true} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(toffset+k)) !=
     Character.toLowerCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(toffset+k)) !=
     *         Character.toUpperCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param   ignoreCase   if {@code true}, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          {@code false} otherwise. Whether the matching is exact
     *          or case insensitive depends on the {@code ignoreCase}
     *          argument.
     */
    /**
     * 1、测试两个字符串区域是否相等。
     *
     * @param ignoreCase 如果true，则在比较字符时忽略大小写。
     * @param toffset    该字符串中子区域的起始偏移量。
     * @param other      字符串参数。
     * @param ooffset    字符串参数中子区域的起始偏移量。
     * @param len        要比较的字符数。
     * @return
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
                                 String other, int ooffset, int len) {
        //当前字符串的字符数组
        char ta[] = value;
        //当前字符串的起始偏移量
        int to = toffset;
        //比较字符串的字符数组
        char pa[] = other.value;
        //比较字符串的起始偏移量
        int po = ooffset;
        // Note: toffset, ooffset, or len might be near -1>>>1.
        //1、当前字符串的起始偏移量小于0或偏移量+ 要比较的字符数大于当前字符串的字符数组的长度
        //2、比较字符串的起始偏移量小于0或偏移量+ 要比较的字符数大于比较字符串的字符数组的长度
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long) value.length - len)
                || (ooffset > (long) other.value.length - len)) {
            return false;
        }
        //按照要比较的字符数进行比较
        while (len-- > 0) {
            //获取第n的字符
            char c1 = ta[to++];
            char c2 = pa[po++];
            //若箱等，则继续下个字符的比较
            if (c1 == c2) {
                continue;
            }
            //
            if (ignoreCase) {
                // If characters don't match but case may be ignored,
                // try converting both characters to uppercase.
                // If the results match, then the comparison scan should
                // continue.
                // 如果字符不匹配，但大小写可能会被忽略，
                // 尝试将两个字符都转换为大写。
                // 如果结果匹配，则比较扫描应继续。
                char u1 = Character.toUpperCase(c1);
                char u2 = Character.toUpperCase(c2);
                if (u1 == u2) {
                    continue;
                }
                // Unfortunately, conversion to uppercase does not work properly
                // for the Georgian alphabet, which has strange rules about case
                // conversion.  So we need to make one last check before
                // exiting.
                //不幸的是，转换为大写字母无法正常工作
                //用于格鲁吉亚字母，该字母对大小写有奇怪的规定转换。 所以我们需要做小写的检查。
                if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                    continue;
                }
            }
            //否则，返回false
            return false;
        }
        //若不存在不相等的，则返回true
        return true;
    }

    /**
     * Tests if the substring of this string beginning at the
     * specified index starts with the specified prefix.
     *
     * @param prefix  the prefix.
     * @param toffset where to begin looking in this string.
     * @return {@code true} if the character sequence represented by the
     * argument is a prefix of the substring of this object starting
     * at index {@code toffset}; {@code false} otherwise.
     * The result is {@code false} if {@code toffset} is
     * negative or greater than the length of this
     * {@code String} object; otherwise the result is the same
     * as the result of the expression
     * <pre>
     *          this.substring(toffset).startsWith(prefix)
     *          </pre>
     */
    /**
     * 1、测试此字符串从指定的下标开始，是否以指定的前缀开头。
     * 2、该方法用于检测字符串是否以某个前缀开始，并且可以指定偏移。
     * @param prefix  前缀
     * @param toffset  从哪里开始寻找这个字符串。
     * @return  从下标toffset开始，如果参数表示的字符序列是此对象的子字符串的前缀，则为true；否则{@code false}。
     *          如果toffset为负或大于此String对象的长度，则结果为false。
     *          否则结果与表达式的结果相同。（表达式为 this.substring(toffset).startsWith(prefix)）
     *
     */
    public boolean startsWith(String prefix, int toffset) {
        //当前字符串的字符数组
        char ta[] = value;
        //偏移量
        int to = toffset;
        //前缀字符串的字符数组
        char pa[] = prefix.value;
        int po = 0;
        //前缀字符串的字符数组的长度
        int pc = prefix.value.length;
        // Note: toffset might be near -1>>>1.
        //检查偏移和长度的合法性
        if ((toffset < 0) || (toffset > value.length - pc)) {
            return false;
        }
        //循环前缀字符串的长度次数
        while (--pc >= 0) {
            //从当前字符串的偏移位下标的开始字符与前缀字符串的字符比较
            //若不相等，则返回false
            if (ta[to++] != pa[po++]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param prefix the prefix.
     * @return {@code true} if the character sequence represented by the
     * argument is a prefix of the character sequence represented by
     * this string; {@code false} otherwise.
     * Note also that {@code true} will be returned if the
     * argument is an empty string or is equal to this
     * {@code String} object as determined by the
     * {@link #equals(Object)} method.
     * @since 1. 0
     */
    /**
     * 1、测试此字符串是否以指定的前缀开头。
     * @param prefix  前缀
     * @return  如果参数表示的字符序列是此字符串表示的字符序列的前缀，则为true； 否则{@code false}。
     *          还请注意，如果参数为空字符串或等于#equals(Object)方法确定的String对象，则将返回true。
     */
    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param suffix the suffix.
     * @return {@code true} if the character sequence represented by the
     * argument is a suffix of the character sequence represented by
     * this object; {@code false} otherwise. Note that the
     * result will be {@code true} if the argument is the
     * empty string or is equal to this {@code String} object
     * as determined by the {@link #equals(Object)} method.
     */
    /**
     * 1、测试此字符串是否以指定的后缀结尾。
     * 2、该方法用于检查是否以某个字符串结尾，间接调用startsWith方法
     * @param suffix  后最
     * @return  如果参数表示的字符序列是此对象表示的字符序列的后缀为true； 否则false。
     *          请注意，如果参数为空字符串或等于#equals(Object)方法确定的String对象，则结果为true。
     */
    public boolean endsWith(String suffix) {
        //调用startsWith方法，比较后几位
        return startsWith(suffix, value.length - suffix.value.length);
    }

    /**
     * Returns a hash code for this string. The hash code for a
     * {@code String} object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using {@code int} arithmetic, where {@code s[i]} is the
     * <i>i</i>th character of the string, {@code n} is the length of
     * the string, and {@code ^} indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return a hash code value for this object.
     */
    /**
     * 1、返回此字符串的哈希码。
     * 2、String对象的哈希码计算为s [0] * 31 ^（n-1）+ s [1] * 31 ^（n-2）+ ... + s [n-1] 使用int算术，
     *    其中s [i]是字符串的第i个字符，n是字符串的长度，^表示幂。
     *   （空字符串的哈希值为零。）
     * @return 此对象的哈希码值。
     */
    public int hashCode() {
        //获取字符串对象的哈希值，默认值为0
        int h = hash;
        //若为hash值为0且当前字符串的字符数组长度大于0
        if (h == 0 && value.length > 0) {
            //当前字符串的字符数组
            char val[] = value;
            //遍历每个字符，计算hash值
            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
            //s [0] * 31 ^（n-1）+ s [1] * 31 ^（n-2）+ ... + s [n-1] ^ 1
            hash = h;
        }
        return h;
    }

    /**
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * {@code ch} occurs in the character sequence represented by
     * this {@code String} object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of {@code ch} in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then {@code -1} is returned.
     *
     * @param ch a character (Unicode code point).
     * @return the index of the first occurrence of the character in the
     * character sequence represented by this object, or
     * {@code -1} if the character does not occur.
     */
    /**
     * 1、返回指定字符首次出现在此字符串中的下标。
     *   如果在此String对象表示的字符序列中出现了值为ch的字符，则将返回第一个此类出现的下标（以Unicode代码为单位）。
     *   对于ch的值，其范围是0到0xFFFF（包括0），这是最小值k，这样：
     *   this.charAt(k) == ch 是真的。
     *   对于ch的其他值，它是最小值k，使得：
     *   this.codePointAt(k) == ch 是真的。
     *   无论哪种情况，如果该字符串中均未出现此类字符，则返回-1。
     * @param ch  字符（Unicode代码点）
     * @return 该对象表示的字符序列中字符首次出现的下标；
     *         如果未出现字符，则返回-1。
     */
    public int indexOf(int ch) {
        //从下标0开始搜索
        return indexOf(ch, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     * <p>
     * If a character with value {@code ch} occurs in the
     * character sequence represented by this {@code String}
     * object at an index no smaller than {@code fromIndex}, then
     * the index of the first such occurrence is returned. For values
     * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
     * this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or after position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p>
     * There is no restriction on the value of {@code fromIndex}. If it
     * is negative, it has the same effect as if it were zero: this entire
     * string may be searched. If it is greater than the length of this
     * string, it has the same effect as if it were equal to the length of
     * this string: {@code -1} is returned.
     *
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param ch        a character (Unicode code point).
     * @param fromIndex the index to start the search from.
     * @return the index of the first occurrence of the character in the
     * character sequence represented by this object that is greater
     * than or equal to {@code fromIndex}, or {@code -1}
     * if the character does not occur.
     */
    /**
     * 1、返回指定字符首次出现在该字符串中的下标，从指定下标开始搜索。
     *   如果在此String对象表示的字符序列中出现了值为ch的字符，且下标不小于fromIndex，则返回第一次出现的下标。
     *   对于ch的值，其范围是0到0xFFFF（包括0），这是最小值k，这样：
     *   (this.charAt(k) == ch)&&(k>= fromIndex)是真的。
     *   对于ch的其他值，它是最小值k，使得：
     *   (this.codePointAt(k) == ch)&&(k >= fromIndex)是真的。
     *   在任何一种情况下，如果在位置fromIndex或之后的字符串中没有这样的字符，则返回-1。
     * 2、fromIndex的值没有限制。
     *   如果为负，则其效果与零相同：可以搜索整个字符串。
     *   如果它大于此字符串的长度，则具有与等于此字符串的长度相同的效果：返回-1。
     * 3、所有下标均以char值（Unicode代码单位）指定。
     *
     * @param ch     字符（Unicode代码点）。
     * @param fromIndex  开始搜索的下标
     * @return 返回在此对象表示的字符序列中首次出现的字符的下标，大于或等于fromIndex或-1（如果没有出现此字符）。
     */
    public int indexOf(int ch, int fromIndex) {
        //max为当前字符串对应的字符数组的长度
        final int max = value.length;
        //指定下标的位置如果小于0，默认从 0 开始搜索
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex >= max) {
            //如果指定下标值大于等于字符的长度（因为是数组，下标最多只能是max-1），直接返回-1
            // Note: fromIndex might be near -1>>>1.
            return -1;
        }

        //一个char占用两个字节，如果ch小于2的16次方（65536），绝大多数字符都在此范围内
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] value = this.value;
            //for循环依次判断字符串每个字符是否和指定字符相等
            for (int i = fromIndex; i < max; i++) {
                if (value[i] == ch) {
                    //相等，则返回下标
                    return i;
                }
            }
            //不存在相等的字符，则返回 -1
            return -1;
        } else {
            //当字符大于 65536时，处理的少数情况，该方法会首先判断是否是有效字符，然后依次进行比较
            return indexOfSupplementary(ch, fromIndex);
        }
    }

    /**
     * Handles (rare) calls of indexOf with a supplementary character.
     */
    /**
     * 处理（罕见）带有补充字符的indexOf调用。
     * @param ch
     * @param fromIndex
     * @return
     */
    private int indexOfSupplementary(int ch, int fromIndex) {
        //若字符合法
        if (Character.isValidCodePoint(ch)) {
            //当前字符串的字符数组
            final char[] value = this.value;
            //高位字符
            final char hi = Character.highSurrogate(ch);
            //低位字符
            final char lo = Character.lowSurrogate(ch);
            //字符数组长度-1（两个字符）
            final int max = value.length - 1;
            //遍历比较每个字符（两个字符一起比较）
            for (int i = fromIndex; i < max; i++) {
                if (value[i] == hi && value[i + 1] == lo) {
                    //若两个字符都相等，则返回下标
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character. For values of {@code ch} in the
     * range from 0 to 0xFFFF (inclusive), the index (in Unicode code
     * units) returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true.  In either case, if no such character occurs in this
     * string, then {@code -1} is returned.  The
     * {@code String} is searched backwards starting at the last
     * character.
     *
     * @param ch a character (Unicode code point).
     * @return the index of the last occurrence of the character in the
     * character sequence represented by this object, or
     * {@code -1} if the character does not occur.
     */
    public int lastIndexOf(int ch) {
        return lastIndexOf(ch, value.length - 1);
    }

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character, searching backward starting at the
     * specified index. For values of {@code ch} in the range
     * from 0 to 0xFFFF (inclusive), the index returned is the largest
     * value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or before position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param ch        a character (Unicode code point).
     * @param fromIndex the index to start the search from. There is no
     *                  restriction on the value of {@code fromIndex}. If it is
     *                  greater than or equal to the length of this string, it has
     *                  the same effect as if it were equal to one less than the
     *                  length of this string: this entire string may be searched.
     *                  If it is negative, it has the same effect as if it were -1:
     *                  -1 is returned.
     * @return the index of the last occurrence of the character in the
     * character sequence represented by this object that is less
     * than or equal to {@code fromIndex}, or {@code -1}
     * if the character does not occur before that point.
     */
    public int lastIndexOf(int ch, int fromIndex) {
        if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
            // handle most cases here (ch is a BMP code point or a
            // negative value (invalid code point))
            final char[] value = this.value;
            int i = Math.min(fromIndex, value.length - 1);
            for (; i >= 0; i--) {
                if (value[i] == ch) {
                    return i;
                }
            }
            return -1;
        } else {
            return lastIndexOfSupplementary(ch, fromIndex);
        }
    }

    /**
     * Handles (rare) calls of lastIndexOf with a supplementary character.
     */
    private int lastIndexOfSupplementary(int ch, int fromIndex) {
        if (Character.isValidCodePoint(ch)) {
            final char[] value = this.value;
            char hi = Character.highSurrogate(ch);
            char lo = Character.lowSurrogate(ch);
            int i = Math.min(fromIndex, value.length - 2);
            for (; i >= 0; i--) {
                if (value[i] == hi && value[i + 1] == lo) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     *
     * <p>The returned index is the smallest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param str the substring to search for.
     * @return the index of the first occurrence of the specified substring,
     * or {@code -1} if there is no such occurrence.
     */
    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * <p>The returned index is the smallest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> &gt;= fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param str       the substring to search for.
     * @param fromIndex the index from which to start the search.
     * @return the index of the first occurrence of the specified substring,
     * starting at the specified index,
     * or {@code -1} if there is no such occurrence.
     */
    public int indexOf(String str, int fromIndex) {
        return indexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    /**
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param fromIndex    the index to begin searching from.
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       String target, int fromIndex) {
        return indexOf(source, sourceOffset, sourceCount,
                target.value, 0, target.value.length,
                fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++)
                    ;

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring.  The last occurrence of the empty string ""
     * is considered to occur at the index value {@code this.length()}.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param str the substring to search for.
     * @return the index of the last occurrence of the specified substring,
     * or {@code -1} if there is no such occurrence.
     */
    public int lastIndexOf(String str) {
        return lastIndexOf(str, value.length);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> {@code <=} fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param str       the substring to search for.
     * @param fromIndex the index to start the search from.
     * @return the index of the last occurrence of the specified substring,
     * searching backward from the specified index,
     * or {@code -1} if there is no such occurrence.
     */
    public int lastIndexOf(String str, int fromIndex) {
        return lastIndexOf(value, 0, value.length,
                str.value, 0, str.value.length, fromIndex);
    }

    /**
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param fromIndex    the index to begin searching from.
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                           String target, int fromIndex) {
        return lastIndexOf(source, sourceOffset, sourceCount,
                target.value, 0, target.value.length,
                fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param source       the characters being searched.
     * @param sourceOffset offset of the source string.
     * @param sourceCount  count of the source string.
     * @param target       the characters being searched for.
     * @param targetOffset offset of the target string.
     * @param targetCount  count of the target string.
     * @param fromIndex    the index to begin searching from.
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                           char[] target, int targetOffset, int targetCount,
                           int fromIndex) {
        /*
         * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
        int rightIndex = sourceCount - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        int strLastIndex = targetOffset + targetCount - 1;
        char strLastChar = target[strLastIndex];
        int min = sourceOffset + targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar:
        while (true) {
            while (i >= min && source[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (source[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start - sourceOffset + 1;
        }
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     *
     * @param beginIndex the beginning index, inclusive.
     * @return the specified substring.
     * @throws IndexOutOfBoundsException if
     *                                   {@code beginIndex} is negative or larger than the
     *                                   length of this {@code String} object.
     */
    public String substring(int beginIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = value.length - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return the specified substring.
     * @exception IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code endIndex} is larger than the length of
     *             this {@code String} object, or
     *             {@code beginIndex} is larger than
     *             {@code endIndex}.
     */

    /**
     * 1、返回一个字符串，该字符串是该字符串的子字符串。
     * 子字符串从指定的{@code beginIndex}开始，并扩展到下标为{@code endIndex-1}处的字符。
     * 因此，子字符串的长度为{@code endIndex-beginIndex}。
     * 2、举例：
     * a、"hamburger".substring(4, 8) returns "urge"
     * b、"smiles".substring(1, 5) returns "mile"
     *
     * @param beginIndex 起始下标（包含）
     * @param endIndex   结束下标（不包含）
     * @return 指定字符串
     */
    public String substring(int beginIndex, int endIndex) {
        //若起始下标为负数，则抛出数组下标越界异常
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }

        //若结束下标大于字符串长度，则抛出数组下标越界异常
        if (endIndex > value.length) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }

        //计算截取的长度
        int subLen = endIndex - beginIndex;

        //若截取的长度为负数，则抛出数组下标越界异常
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        //若起始下标为0且结束下标为字符串长度，则是要获取当前字符串，直接返回this字符串即可
        //否则调用String的构造函数
        return ((beginIndex == 0) && (endIndex == value.length)) ? this
                : new String(value, beginIndex, subLen);
    }

    /**
     * Returns a character sequence that is a subsequence of this sequence.
     *
     * <p> An invocation of this method of the form
     *
     * <blockquote><pre>
     * str.subSequence(begin,&nbsp;end)</pre></blockquote>
     * <p>
     * behaves in exactly the same way as the invocation
     *
     * <blockquote><pre>
     * str.substring(begin,&nbsp;end)</pre></blockquote>
     *
     * @param beginIndex the begin index, inclusive.
     * @param endIndex   the end index, exclusive.
     * @return the specified subsequence.
     * @throws IndexOutOfBoundsException if {@code beginIndex} or {@code endIndex} is negative,
     *                                   if {@code endIndex} is greater than {@code length()},
     *                                   or if {@code beginIndex} is greater than {@code endIndex}
     * @apiNote This method is defined so that the {@code String} class can implement
     * the {@link CharSequence} interface.
     * @spec JSR-51
     * @since 1.4
     */
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return this.substring(beginIndex, endIndex);
    }

    /**
     * Concatenates the specified string to the end of this string.
     * <p>
     * If the length of the argument string is {@code 0}, then this
     * {@code String} object is returned. Otherwise, a
     * {@code String} object is returned that represents a character
     * sequence that is the concatenation of the character sequence
     * represented by this {@code String} object and the character
     * sequence represented by the argument string.<p>
     * Examples:
     * <blockquote><pre>
     * "cares".concat("s") returns "caress"
     * "to".concat("get").concat("her") returns "together"
     * </pre></blockquote>
     *
     * @param str the {@code String} that is concatenated to the end
     *            of this {@code String}.
     * @return a string that represents the concatenation of this object's
     * characters followed by the string argument's characters.
     */
    public String concat(String str) {
        int otherLen = str.length();
        if (otherLen == 0) {
            return this;
        }
        int len = value.length;
        char buf[] = Arrays.copyOf(value, len + otherLen);
        str.getChars(buf, len);
        return new String(buf, true);
    }

    /**
     * Returns a string resulting from replacing all occurrences of
     * {@code oldChar} in this string with {@code newChar}.
     * <p>
     * If the character {@code oldChar} does not occur in the
     * character sequence represented by this {@code String} object,
     * then a reference to this {@code String} object is returned.
     * Otherwise, a {@code String} object is returned that
     * represents a character sequence identical to the character sequence
     * represented by this {@code String} object, except that every
     * occurrence of {@code oldChar} is replaced by an occurrence
     * of {@code newChar}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "mesquite in your cellar".replace('e', 'o')
     *         returns "mosquito in your collar"
     * "the war of baronets".replace('r', 'y')
     *         returns "the way of bayonets"
     * "sparring with a purple porpoise".replace('p', 't')
     *         returns "starring with a turtle tortoise"
     * "JonL".replace('q', 'x') returns "JonL" (no change)
     * </pre></blockquote>
     *
     * @param   oldChar   the old character.
     * @param   newChar   the new character.
     * @return a string derived from this string by replacing every
     *          occurrence of {@code oldChar} with {@code newChar}.
     */
    /**
     * 1、返回一个字符串，该字符串是通过用newChar替换此字符串中所有出现的oldChar而产生的。
     * 如果在此String对象表示的字符序列中未出现字符oldChar，则返回对该String对象的引用。
     * 否则，返回一个String对象，该对象表示与该String对象表示的字符序列相同的字符序列，除了每个oldChar字符替换为newChar。
     * 2、举例
     * a、"mesquite in your cellar".replace('e', 'o')  returns "mosquito in your collar"
     * b、"the war of baronets".replace('r', 'y') returns "the way of bayonets"
     * c、"sparring with a purple porpoise".replace('p', 't') returns "starring with a turtle tortoise"
     * d、"JonL".replace('q', 'x')   returns "JonL" (no change)
     *
     * @param oldChar 旧的字符
     * @param newChar 新的字符
     * @return 通过用newChar替换每次出现的oldChar，从而得到从源字符串派生出新字符串
     */
    public String replace(char oldChar, char newChar) {
        //判断旧的字符和新的字符是否相同，若相同，则直接返回当前String对象即可
        if (oldChar != newChar) {
            int len = value.length;
            int i = -1;
            //避免getfield
            char[] val = value; /* avoid getfield opcode */

            //遍历当前字符串是否存在旧的字符，存在的话，i会小于当前字符串的长度，否则直接返回当前String对象
            while (++i < len) {
                if (val[i] == oldChar) {
                    break;
                }
            }
            if (i < len) {
                char buf[] = new char[len];
                //遍历前面不是旧字符的字符，并且保存到新的char[]数组中
                for (int j = 0; j < i; j++) {
                    buf[j] = val[j];
                }
                //遍历剩下的字符数组
                while (i < len) {
                    char c = val[i];
                    //若当前字符是旧字符，则在新的char[]数组中增加新字符，否则保存当前字符即可
                    buf[i] = (c == oldChar) ? newChar : c;
                    i++;
                }
                //根据新的字符数组创建String对象
                return new String(buf, true);
            }
        }
        return this;
    }

    /**
     * Tells whether or not this string matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .matches(}<i>regex</i>{@code )} yields exactly the
     * same result as the expression
     *
     * <blockquote>
     * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#matches(String, CharSequence)
     * matches(<i>regex</i>, <i>str</i>)}
     * </blockquote>
     *
     * @param regex the regular expression to which this string is to be matched
     * @return {@code true} if, and only if, this string matches the
     * given regular expression
     * @throws PatternSyntaxException if the regular expression's syntax is invalid
     * @spec JSR-51
     * @see java.util.regex.Pattern
     * @since 1.4
     */
    public boolean matches(String regex) {
        return Pattern.matches(regex, this);
    }

    /**
     * Returns true if and only if this string contains the specified
     * sequence of char values.
     *
     * @param s the sequence to search for
     * @return true if this string contains {@code s}, false otherwise
     * @since 1.5
     */
    public boolean contains(CharSequence s) {
        return indexOf(s.toString()) > -1;
    }

    /**
     * Replaces the first substring of this string that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .replaceFirst(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     * yields exactly the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceFirst replaceFirst}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
     * replacement string may cause the results to be different than if it were
     * being treated as a literal replacement string; see
     * {@link java.util.regex.Matcher#replaceFirst}.
     * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
     * meaning of these characters, if desired.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     * @param   replacement
     *          the string to be substituted for the first match
     *
     * @return The resulting {@code String}
     *
     * @throws PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    /**
     * 1、用给定的替换项替换与给定的<a href="../util/regex/Pattern.html#sum">正则表达式</a>匹配的该字符串的第一个子字符串。
     * 注意：替换字符串中的反斜杠（{@code \}）和美元符号（{@code $}）可能导致结果与被视为文字替换字符串的结果有所不同；
     * 请参阅{@link java.util.regex.Matcher＃replaceFirst}。
     * 如果需要，请使用{@link java.util.regex.Matcher＃quoteReplacement}取消显示这些字符的特殊含义。
     *
     * @param regex       此字符串要匹配的正则表达式
     * @param replacement 要替换第一个匹配项的字符串
     * @return 一个字符串
     */
    public String replaceFirst(String regex, String replacement) {
        return Pattern.compile(regex).matcher(this).replaceFirst(replacement);
    }

    /**
     * Replaces each substring of this string that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .replaceAll(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     * yields exactly the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceAll replaceAll}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
     * replacement string may cause the results to be different than if it were
     * being treated as a literal replacement string; see
     * {@link java.util.regex.Matcher#replaceAll Matcher.replaceAll}.
     * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
     * meaning of these characters, if desired.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     * @param   replacement
     *          the string to be substituted for each match
     *
     * @return The resulting {@code String}
     *
     * @throws PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    /**
     * 1、用给定的替换项替换与给定的<a href="../util/regex/Pattern.html#sum">正则表达式</a>匹配的该字符串的每个子字符串。
     * 注意：替换字符串中的反斜杠（{@code \}）和美元符号（{@code $}）可能导致结果与被视为文字替换字符串的结果有所不同；
     * 请参阅java.util.regex.Matcher＃replaceAll Matcher.replaceAll。
     * 如果需要，可以使用 java.util.regex.Matcher＃quoteReplacement取消显示这些字符的特殊含义。
     *
     * @param regex       该字符串要与之匹配的正则表达式
     * @param replacement 每次匹配的字符串要替换成的字符串
     * @return 一个字符串
     */
    public String replaceAll(String regex, String replacement) {
        return Pattern.compile(regex).matcher(this).replaceAll(replacement);
    }

    /**
     * Replaces each substring of this string that matches the literal target
     * sequence with the specified literal replacement sequence. The
     * replacement proceeds from the beginning of the string to the end, for
     * example, replacing "aa" with "b" in the string "aaa" will result in
     * "ba" rather than "ab".
     *
     * @param  target The sequence of char values to be replaced
     * @param  replacement The replacement sequence of char values
     * @return The resulting string
     * @since 1.5
     */
    /**
     * 1、用指定的文字替换序列替换该字符串中与文字目标序列匹配的每个子字符串。替换从字符串的开头到结尾进行。
     * 2、例如，在字符串“ aaa”中将“ aa”替换为“ b”将得到“ ba”而不是“ ab”。
     *
     * @param target      被替换的char值的序列
     * @param replacement 进行替换的char值的序列
     * @return 一个字符串对象
     */
    public String replace(CharSequence target, CharSequence replacement) {
        //Matcher.quoteReplacement方法取消显示这些字符的特殊含义。（这些字符包括反斜杠、美元符等）
        return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
                this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }

    /**
     * Splits this string around matches of the given
     * <a href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> The array returned by this method contains each substring of this
     * string that is terminated by another substring that matches the given
     * expression or is terminated by the end of the string.  The substrings in
     * the array are in the order in which they occur in this string.  If the
     * expression does not match any part of the input then the resulting array
     * has just one element, namely this string.
     *
     * <p> When there is a positive-width match at the beginning of this
     * string then an empty leading substring is included at the beginning
     * of the resulting array. A zero-width match at the beginning however
     * never produces such empty leading substring.
     *
     * <p> The {@code limit} parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.  If the limit <i>n</i> is greater than zero then the pattern
     * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
     * length will be no greater than <i>n</i>, and the array's last entry
     * will contain all input beyond the last matched delimiter.  If <i>n</i>
     * is non-positive then the pattern will be applied as many times as
     * possible and the array can have any length.  If <i>n</i> is zero then
     * the pattern will be applied as many times as possible, the array can
     * have any length, and trailing empty strings will be discarded.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the
     * following results with these parameters:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split example showing regex, limit, and result">
     * <tr>
     *     <th>Regex</th>
     *     <th>Limit</th>
     *     <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     *     <td align=center>2</td>
     *     <td>{@code { "boo", "and:foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>5</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>5</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>0</td>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     * <p> An invocation of this method of the form
     * <i>str.</i>{@code split(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
     * yields the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#split(java.lang.CharSequence, int) split}(<i>str</i>,&nbsp;<i>n</i>)
     * </code>
     * </blockquote>
     *
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @param  limit
     *         the result threshold, as described above
     *
     * @return the array of strings computed by splitting this string
     *          around matches of the given regular expression
     *
     * @throws PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     */
    /**
     * 1、在给定的<a href="../util/regex/Pattern.html#sum">正则表达式</a>的匹配项周围拆分此字符串。
     * 2、此方法返回的数组包含此字符串的每个子字符串，该子字符串由与给定表达式匹配的另一个子字符串终止或由字符串的结尾终止。
     * 数组中的子字符串按照它们在此字符串中出现的顺序排列。
     * 如果表达式与输入的任何部分都不匹配，则结果数组只有一个元素，即此字符串。
     * 3、如果此字符串的开头存在正好匹配的字符串，则在结果数组的开头将包含一个空的前导子字符串。
     * 开头的没有匹配上永远不会产生这样的空的子字符串。
     * 4、
     *
     * @param regex 分隔的正则表达式
     * @param limit 拆分的个数上限
     * @return
     */
    public String[] split(String regex, int limit) {
        /* fastpath if the regex is a
         (1)one-char String and this character is not one of the
            RegEx's meta characters ".$|()[{^?*+\\", or
         (2)two-char String and the first char is the backslash and
            the second is not the ascii digit or ascii letter.
         */
        char ch = 0;
        /*
          如果正则表达式是:
           a、一个字符的字符串，并且此字符不是以下字符之一".$|()[{^?*+\\"
           b、两个字符的字符串，第一个字符是反斜杠"\"，第二个大小写字母或者数字。
         */
        if (((regex.value.length == 1 &&
                ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
                (regex.length() == 2 &&
                        regex.charAt(0) == '\\' &&
                        (((ch = regex.charAt(1)) - '0') | ('9' - ch)) < 0 &&
                        ((ch - 'a') | ('z' - ch)) < 0 &&
                        ((ch - 'A') | ('Z' - ch)) < 0)) &&
                (ch < Character.MIN_HIGH_SURROGATE ||
                        ch > Character.MAX_LOW_SURROGATE)) {
            int off = 0;
            int next = 0;
            //大于0，limited==true,反之limited==false
            boolean limited = limit > 0;
            ArrayList<String> list = new ArrayList<>();
            while ((next = indexOf(ch, off)) != -1) {
                //当参数limit<=0 或者 集合list的长度小于 limit-1
                if (!limited || list.size() < limit - 1) {
                    list.add(substring(off, next));
                    off = next + 1;
                } else {    // last one
                    //assert (list.size() == limit - 1);
                    //判断最后一个list.size() == limit - 1
                    list.add(substring(off, value.length));
                    off = value.length;
                    break;
                }
            }
            // If no match was found, return this
            //如果没有一个能匹配的，返回一个新的字符串，内容和原来的一样
            if (off == 0)
                return new String[]{this};

            // Add remaining segment
            // 当 limit<=0 时，limited==false,或者集合的长度 小于 limit是，截取添加剩下的字符串
            if (!limited || list.size() < limit)
                list.add(substring(off, value.length));

            // Construct result
            // 当 limit == 0 时，如果末尾添加的元素为空（长度为0），则集合长度不断减1，直到末尾不为空
            int resultSize = list.size();
            if (limit == 0) {
                while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
                    resultSize--;
                }
            }
            String[] result = new String[resultSize];
            return list.subList(0, resultSize).toArray(result);
        }
        return Pattern.compile(regex).split(this, limit);
    }

    /**
     * Splits this string around matches of the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> This method works as if by invoking the two-argument {@link
     * #split(String, int) split} method with the given expression and a limit
     * argument of zero.  Trailing empty strings are therefore not included in
     * the resulting array.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the following
     * results with these expressions:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split examples showing regex and result">
     * <tr>
     * <th>Regex</th>
     * <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     * <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     * <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     * @param regex the delimiting regular expression
     * @return the array of strings computed by splitting this string
     * around matches of the given regular expression
     * @throws PatternSyntaxException if the regular expression's syntax is invalid
     * @spec JSR-51
     * @see java.util.regex.Pattern
     * @since 1.4
     */
    public String[] split(String regex) {
        return split(regex, 0);
    }

    /**
     * Returns a new String composed of copies of the
     * {@code CharSequence elements} joined together with a copy of
     * the specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     String message = String.join("-", "Java", "is", "cool");
     *     // message returned is: "Java-is-cool"
     * }</pre></blockquote>
     * <p>
     * Note that if an element is null, then {@code "null"} is added.
     *
     * @param delimiter the delimiter that separates each element
     * @param elements  the elements to join together.
     * @return a new {@code String} that is composed of the {@code elements}
     * separated by the {@code delimiter}
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *                              is {@code null}
     * @see java.util.StringJoiner
     * @since 1.8
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        // Number of elements not likely worth Arrays.stream overhead.
        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence cs : elements) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    /**
     * Returns a new {@code String} composed of copies of the
     * {@code CharSequence elements} joined together with a copy of the
     * specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     List<String> strings = new LinkedList<>();
     *     strings.add("Java");strings.add("is");
     *     strings.add("cool");
     *     String message = String.join(" ", strings);
     *     //message returned is: "Java is cool"
     *
     *     Set<String> strings = new LinkedHashSet<>();
     *     strings.add("Java"); strings.add("is");
     *     strings.add("very"); strings.add("cool");
     *     String message = String.join("-", strings);
     *     //message returned is: "Java-is-very-cool"
     * }</pre></blockquote>
     * <p>
     * Note that if an individual element is {@code null}, then {@code "null"} is added.
     *
     * @param delimiter a sequence of characters that is used to separate each
     *                  of the {@code elements} in the resulting {@code String}
     * @param elements  an {@code Iterable} that will have its {@code elements}
     *                  joined together.
     * @return a new {@code String} that is composed from the {@code elements}
     * argument
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *                              is {@code null}
     * @see #join(CharSequence, CharSequence...)
     * @see java.util.StringJoiner
     * @since 1.8
     */
    public static String join(CharSequence delimiter,
                              Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence cs : elements) {
            joiner.add(cs);
        }
        return joiner.toString();
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the given {@code Locale}.  Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of lowercase  mappings are in the following table:
     * <table border="1" summary="Lowercase mapping examples showing language code of locale, upper case, lower case, and description">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Upper Case</th>
     *   <th>Lower Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0130</td>
     *   <td>&#92;u0069</td>
     *   <td>capital letter I with dot above -&gt; small letter i</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0049</td>
     *   <td>&#92;u0131</td>
     *   <td>capital letter I -&gt; small letter dotless i </td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>French Fries</td>
     *   <td>french fries</td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td><img src="doc-files/capiota.gif" alt="capiota"><img src="doc-files/capchi.gif" alt="capchi">
     *       <img src="doc-files/captheta.gif" alt="captheta"><img src="doc-files/capupsil.gif" alt="capupsil">
     *       <img src="doc-files/capsigma.gif" alt="capsigma"></td>
     *   <td><img src="doc-files/iota.gif" alt="iota"><img src="doc-files/chi.gif" alt="chi">
     *       <img src="doc-files/theta.gif" alt="theta"><img src="doc-files/upsilon.gif" alt="upsilon">
     *       <img src="doc-files/sigma1.gif" alt="sigma"></td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * </table>
     *
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to lowercase.
     * @see java.lang.String#toLowerCase()
     * @see java.lang.String#toUpperCase()
     * @see java.lang.String#toUpperCase(Locale)
     * @since 1.1
     */
    public String toLowerCase(Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        int firstUpper;
        final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
        scan:
        {
            for (firstUpper = 0; firstUpper < len; ) {
                char c = value[firstUpper];
                if ((c >= Character.MIN_HIGH_SURROGATE)
                        && (c <= Character.MAX_HIGH_SURROGATE)) {
                    int supplChar = codePointAt(firstUpper);
                    if (supplChar != Character.toLowerCase(supplChar)) {
                        break scan;
                    }
                    firstUpper += Character.charCount(supplChar);
                } else {
                    if (c != Character.toLowerCase(c)) {
                        break scan;
                    }
                    firstUpper++;
                }
            }
            return this;
        }

        char[] result = new char[len];
        int resultOffset = 0;  /* result may grow, so i+resultOffset
         * is the write location in result */

        /* Just copy the first few lowerCase characters. */
        System.arraycopy(value, 0, result, 0, firstUpper);

        String lang = locale.getLanguage();
        boolean localeDependent =
                (lang == "tr" || lang == "az" || lang == "lt");
        char[] lowerCharArray;
        int lowerChar;
        int srcChar;
        int srcCount;
        for (int i = firstUpper; i < len; i += srcCount) {
            srcChar = (int) value[i];
            if ((char) srcChar >= Character.MIN_HIGH_SURROGATE
                    && (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
                srcChar = codePointAt(i);
                srcCount = Character.charCount(srcChar);
            } else {
                srcCount = 1;
            }
            if (localeDependent ||
                    srcChar == '\u03A3' || // GREEK CAPITAL LETTER SIGMA
                    srcChar == '\u0130') { // LATIN CAPITAL LETTER I WITH DOT ABOVE
                lowerChar = ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
            } else {
                lowerChar = Character.toLowerCase(srcChar);
            }
            if ((lowerChar == Character.ERROR)
                    || (lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
                if (lowerChar == Character.ERROR) {
                    lowerCharArray =
                            ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
                } else if (srcCount == 2) {
                    resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
                    continue;
                } else {
                    lowerCharArray = Character.toChars(lowerChar);
                }

                /* Grow result if needed */
                int mapLen = lowerCharArray.length;
                if (mapLen > srcCount) {
                    char[] result2 = new char[result.length + mapLen - srcCount];
                    System.arraycopy(result, 0, result2, 0, i + resultOffset);
                    result = result2;
                }
                for (int x = 0; x < mapLen; ++x) {
                    result[i + resultOffset + x] = lowerCharArray[x];
                }
                resultOffset += (mapLen - srcCount);
            } else {
                result[i + resultOffset] = (char) lowerChar;
            }
        }
        return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the default locale. This is equivalent to calling
     * {@code toLowerCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "TITLE".toLowerCase()} in a Turkish locale
     * returns {@code "t\u005Cu0131tle"}, where '\u005Cu0131' is the
     * LATIN SMALL LETTER DOTLESS I character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toLowerCase(Locale.ROOT)}.
     * <p>
     *
     * @return the {@code String}, converted to lowercase.
     * @see java.lang.String#toLowerCase(Locale)
     */
    public String toLowerCase() {
        return toLowerCase(Locale.getDefault());
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the given {@code Locale}. Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of locale-sensitive and 1:M case mappings are in the following table.
     *
     * <table border="1" summary="Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Lower Case</th>
     *   <th>Upper Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0069</td>
     *   <td>&#92;u0130</td>
     *   <td>small letter i -&gt; capital letter I with dot above</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0131</td>
     *   <td>&#92;u0049</td>
     *   <td>small letter dotless i -&gt; capital letter I</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>&#92;u00df</td>
     *   <td>&#92;u0053 &#92;u0053</td>
     *   <td>small letter sharp s -&gt; two letters: SS</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>Fahrvergn&uuml;gen</td>
     *   <td>FAHRVERGN&Uuml;GEN</td>
     *   <td></td>
     * </tr>
     * </table>
     *
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to uppercase.
     * @see java.lang.String#toUpperCase()
     * @see java.lang.String#toLowerCase()
     * @see java.lang.String#toLowerCase(Locale)
     * @since 1.1
     */
    public String toUpperCase(Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }

        int firstLower;
        final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
        scan:
        {
            for (firstLower = 0; firstLower < len; ) {
                int c = (int) value[firstLower];
                int srcCount;
                if ((c >= Character.MIN_HIGH_SURROGATE)
                        && (c <= Character.MAX_HIGH_SURROGATE)) {
                    c = codePointAt(firstLower);
                    srcCount = Character.charCount(c);
                } else {
                    srcCount = 1;
                }
                int upperCaseChar = Character.toUpperCaseEx(c);
                if ((upperCaseChar == Character.ERROR)
                        || (c != upperCaseChar)) {
                    break scan;
                }
                firstLower += srcCount;
            }
            return this;
        }

        /* result may grow, so i+resultOffset is the write location in result */
        int resultOffset = 0;
        char[] result = new char[len]; /* may grow */

        /* Just copy the first few upperCase characters. */
        System.arraycopy(value, 0, result, 0, firstLower);

        String lang = locale.getLanguage();
        boolean localeDependent =
                (lang == "tr" || lang == "az" || lang == "lt");
        char[] upperCharArray;
        int upperChar;
        int srcChar;
        int srcCount;
        for (int i = firstLower; i < len; i += srcCount) {
            srcChar = (int) value[i];
            if ((char) srcChar >= Character.MIN_HIGH_SURROGATE &&
                    (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
                srcChar = codePointAt(i);
                srcCount = Character.charCount(srcChar);
            } else {
                srcCount = 1;
            }
            if (localeDependent) {
                upperChar = ConditionalSpecialCasing.toUpperCaseEx(this, i, locale);
            } else {
                upperChar = Character.toUpperCaseEx(srcChar);
            }
            if ((upperChar == Character.ERROR)
                    || (upperChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
                if (upperChar == Character.ERROR) {
                    if (localeDependent) {
                        upperCharArray =
                                ConditionalSpecialCasing.toUpperCaseCharArray(this, i, locale);
                    } else {
                        upperCharArray = Character.toUpperCaseCharArray(srcChar);
                    }
                } else if (srcCount == 2) {
                    resultOffset += Character.toChars(upperChar, result, i + resultOffset) - srcCount;
                    continue;
                } else {
                    upperCharArray = Character.toChars(upperChar);
                }

                /* Grow result if needed */
                int mapLen = upperCharArray.length;
                if (mapLen > srcCount) {
                    char[] result2 = new char[result.length + mapLen - srcCount];
                    System.arraycopy(result, 0, result2, 0, i + resultOffset);
                    result = result2;
                }
                for (int x = 0; x < mapLen; ++x) {
                    result[i + resultOffset + x] = upperCharArray[x];
                }
                resultOffset += (mapLen - srcCount);
            } else {
                result[i + resultOffset] = (char) upperChar;
            }
        }
        return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the default locale. This method is equivalent to
     * {@code toUpperCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "title".toUpperCase()} in a Turkish locale
     * returns {@code "T\u005Cu0130TLE"}, where '\u005Cu0130' is the
     * LATIN CAPITAL LETTER I WITH DOT ABOVE character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toUpperCase(Locale.ROOT)}.
     * <p>
     *
     * @return the {@code String}, converted to uppercase.
     * @see java.lang.String#toUpperCase(Locale)
     */
    public String toUpperCase() {
        return toUpperCase(Locale.getDefault());
    }

    /**
     * Returns a string whose value is this string, with any leading and trailing
     * whitespace removed.
     * <p>
     * If this {@code String} object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this {@code String} object both have codes
     * greater than {@code '\u005Cu0020'} (the space character), then a
     * reference to this {@code String} object is returned.
     * <p>
     * Otherwise, if there is no character with a code greater than
     * {@code '\u005Cu0020'} in the string, then a
     * {@code String} object representing an empty string is
     * returned.
     * <p>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is greater than {@code '\u005Cu0020'}, and let
     * <i>m</i> be the index of the last character in the string whose code
     * is greater than {@code '\u005Cu0020'}. A {@code String}
     * object is returned, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * {@code this.substring(k, m + 1)}.
     * <p>
     * This method may be used to trim whitespace (as defined above) from
     * the beginning and end of a string.
     *
     * @return A string whose value is this string, with any leading and trailing white
     * space removed, or this string if it has no leading or
     * trailing white space.
     */
    public String trim() {
        int len = value.length;
        int st = 0;
        char[] val = value;    /* avoid getfield opcode */

        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
    }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return the string itself.
     */
    public String toString() {
        return this;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return a newly allocated character array whose length is the length
     * of this string and whose contents are initialized to contain
     * the character sequence represented by this string.
     */
    public char[] toCharArray() {
        // Cannot use Arrays.copyOf because of class initialization order issues
        char result[] = new char[value.length];
        System.arraycopy(value, 0, result, 0, value.length);
        return result;
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     *
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}.
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               {@code null} argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     * @return A formatted string
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @see java.util.Formatter
     * @since 1.5
     */
    public static String format(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

    /**
     * Returns a formatted string using the specified locale, format string,
     * and arguments.
     *
     * @param l      The {@linkplain java.util.Locale locale} to apply during
     *               formatting.  If {@code l} is {@code null} then no localization
     *               is applied.
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               {@code null} argument depends on the
     *               <a href="../util/Formatter.html#syntax">conversion</a>.
     * @return A formatted string
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification
     * @see java.util.Formatter
     * @since 1.5
     */
    public static String format(Locale l, String format, Object... args) {
        return new Formatter(l).format(format, args).toString();
    }

    /**
     * Returns the string representation of the {@code Object} argument.
     *
     * @param obj an {@code Object}.
     * @return if the argument is {@code null}, then a string equal to
     * {@code "null"}; otherwise, the value of
     * {@code obj.toString()} is returned.
     * @see java.lang.Object#toString()
     */
    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    /**
     * Returns the string representation of the {@code char} array
     * argument. The contents of the character array are copied; subsequent
     * modification of the character array does not affect the returned
     * string.
     *
     * @param data the character array.
     * @return a {@code String} that contains the characters of the
     * character array.
     */
    public static String valueOf(char data[]) {
        return new String(data);
    }

    /**
     * Returns the string representation of a specific subarray of the
     * {@code char} array argument.
     * <p>
     * The {@code offset} argument is the index of the first
     * character of the subarray. The {@code count} argument
     * specifies the length of the subarray. The contents of the subarray
     * are copied; subsequent modification of the character array does not
     * affect the returned string.
     *
     * @param data   the character array.
     * @param offset initial offset of the subarray.
     * @param count  length of the subarray.
     * @return a {@code String} that contains the characters of the
     * specified subarray of the character array.
     * @throws IndexOutOfBoundsException if {@code offset} is
     *                                   negative, or {@code count} is negative, or
     *                                   {@code offset+count} is larger than
     *                                   {@code data.length}.
     */
    public static String valueOf(char data[], int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[], int, int)}.
     *
     * @param data   the character array.
     * @param offset initial offset of the subarray.
     * @param count  length of the subarray.
     * @return a {@code String} that contains the characters of the
     * specified subarray of the character array.
     * @throws IndexOutOfBoundsException if {@code offset} is
     *                                   negative, or {@code count} is negative, or
     *                                   {@code offset+count} is larger than
     *                                   {@code data.length}.
     */
    public static String copyValueOf(char data[], int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[])}.
     *
     * @param data the character array.
     * @return a {@code String} that contains the characters of the
     * character array.
     */
    public static String copyValueOf(char data[]) {
        return new String(data);
    }

    /**
     * Returns the string representation of the {@code boolean} argument.
     *
     * @param b a {@code boolean}.
     * @return if the argument is {@code true}, a string equal to
     * {@code "true"} is returned; otherwise, a string equal to
     * {@code "false"} is returned.
     */
    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * Returns the string representation of the {@code char}
     * argument.
     *
     * @param c a {@code char}.
     * @return a string of length {@code 1} containing
     * as its single character the argument {@code c}.
     */
    public static String valueOf(char c) {
        char data[] = {c};
        return new String(data, true);
    }

    /**
     * Returns the string representation of the {@code int} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Integer.toString} method of one argument.
     *
     * @param i an {@code int}.
     * @return a string representation of the {@code int} argument.
     * @see java.lang.Integer#toString(int, int)
     */
    public static String valueOf(int i) {
        return Integer.toString(i);
    }

    /**
     * Returns the string representation of the {@code long} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Long.toString} method of one argument.
     *
     * @param l a {@code long}.
     * @return a string representation of the {@code long} argument.
     * @see java.lang.Long#toString(long)
     */
    public static String valueOf(long l) {
        return Long.toString(l);
    }

    /**
     * Returns the string representation of the {@code float} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Float.toString} method of one argument.
     *
     * @param f a {@code float}.
     * @return a string representation of the {@code float} argument.
     * @see java.lang.Float#toString(float)
     */
    public static String valueOf(float f) {
        return Float.toString(f);
    }

    /**
     * Returns the string representation of the {@code double} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Double.toString} method of one argument.
     *
     * @param d a {@code double}.
     * @return a  string representation of the {@code double} argument.
     * @see java.lang.Double#toString(double)
     */
    public static String valueOf(double d) {
        return Double.toString(d);
    }

    /**
     * Returns a canonical representation for the string object.
     * <p>
     * A pool of strings, initially empty, is maintained privately by the
     * class {@code String}.
     * <p>
     * When the intern method is invoked, if the pool already contains a
     * string equal to this {@code String} object as determined by
     * the {@link #equals(Object)} method, then the string from the pool is
     * returned. Otherwise, this {@code String} object is added to the
     * pool and a reference to this {@code String} object is returned.
     * <p>
     * It follows that for any two strings {@code s} and {@code t},
     * {@code s.intern() == t.intern()} is {@code true}
     * if and only if {@code s.equals(t)} is {@code true}.
     * <p>
     * All literal strings and string-valued constant expressions are
     * interned. String literals are defined in section 3.10.5 of the
     * <cite>The Java&trade; Language Specification</cite>.
     *
     * @return a string that has the same contents as this string, but is
     * guaranteed to be from a pool of unique strings.
     */
    public native String intern();
}
