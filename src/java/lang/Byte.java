/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

/**
 *
 * The {@code Byte} class wraps a value of primitive type {@code byte}
 * in an object.  An object of type {@code Byte} contains a single
 * field whose type is {@code byte}.
 *
 * <p>In addition, this class provides several methods for converting
 * a {@code byte} to a {@code String} and a {@code String} to a {@code
 * byte}, as well as other constants and methods useful when dealing
 * with a {@code byte}.
 *
 * @author  Nakul Saraiya
 * @author  Joseph D. Darcy
 * @see     java.lang.Number
 * @since   JDK1.1
 */

/**
 *  1、{@code Byte}类在对象中包装了原始类型{@code byte}的值。
 *  2、类型为{@code Byte}的对象包含一个类型为{@code byte}的字段。
 *  3、此外，此类提供了几种将{@code byte}转换为{@code String}和将{@code String}转换为{@code Byte}的方法，以及在以下情况下有用的其他常量和方法。
 *  4、Byte类主要的作用就是对基本类型byte进行封装，提供了一些处理byte类型的方法。
 *    比如：byte到String类型的转换方法或String类型到byte类型的转换方法，当然也包含与其他类型之间的转换方法。
 *  5、也是一个不可变类
 */
public final class Byte extends Number implements Comparable<Byte> {

    /**
     * A constant holding the minimum value a {@code byte} can
     * have, -2<sup>7</sup>.
     */
    /**
     * 一个常数，它保存{@code byte}可以具有的最小值-2的7次方。
     */
    public static final byte   MIN_VALUE = -128;

    /**
     * A constant holding the maximum value a {@code byte} can
     * have, 2<sup>7</sup>-1.
     */
    /**
     *  一个常数，它保存{@code byte}可以具有的最大值2的7次方-1。
     */
    public static final byte   MAX_VALUE = 127;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code byte}.
     */
    /**
     * 1、代表原始类型{@code byte}的{@code Class}实例。
     * 2、Class的getPrimitiveClass是一个native方法，在Class.c中有个Java_java_lang_Class_getPrimitiveClass方法与之对应，所以JVM层面会通过JVM_FindPrimitiveClass函数会根据”byte”字符串获得jclass，最终到Java层则为Class<Byte>。
     */
    @SuppressWarnings("unchecked")
    public static final Class<Byte>     TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");

    /**
     * Returns a new {@code String} object representing the
     * specified {@code byte}. The radix is assumed to be 10.
     *
     * @param b the {@code byte} to be converted
     * @return the string representation of the specified {@code byte}
     * @see java.lang.Integer#toString(int)
     */
    /**
     * 1、返回表示指定的{@code byte}的新{@code String}对象。 假设为十进制。
     * 2、当前是个静态方法。
     * @param b  进行转换{@code byte}
     * @return   指定的{@code byte}的字符串表示形式
     */
    public static String toString(byte b) {
        return Integer.toString((int)b, 10);
    }

    /**
     * 1、内部静态类ByteCache
     * 2、它就是一个包含了byte所有可能值的Byte数组。
     * 3、对于byte来说其实它的可能值就是从-128到127，一共256个，所以我们只需要实例化256个Byte对象就可以表示所有可能的byte。
     *    a、而且这些都是静态且final的，避免重复的实例化和回收。
     */
    private static class ByteCache {
        private ByteCache(){}

        //初始化一个(128+127+1)大小的Byte数组(因为有个0)
        static final Byte cache[] = new Byte[-(-128) + 127 + 1];

        //静态代码块填充Byte缓存数组中的值   注意：执行顺序：静态代码块>构造代码块>构造方法
            //静态代码块：最早执行，类被载入内存时执行，只执行一次。没有名字、参数和返回值，有关键字static。
            //构造代码块：执行时间比静态代码块晚，比构造函数早，和构造函数一样，只在对象初始化的时候运行。没有名字、参数和返回值。
            //构造函数： 执行时间比构造代码块时间晚，也是在对象初始化的时候运行。没有返回值，构造函数名称和类名一致。
        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Byte((byte)(i - 128));
        }
    }

    /**
     * Returns a {@code Byte} instance representing the specified
     * {@code byte} value.
     * If a new {@code Byte} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Byte(byte)}, as this method is likely to yield
     * significantly better space and time performance since
     * all byte values are cached.
     *
     * @param  b a byte value.
     * @return a {@code Byte} instance representing {@code b}.
     * @since  1.5
     */
    /**
     * 1、返回表示指定的{@code byte}值的{@code Byte}实例。
     *   如果不需要新的{@code Byte}实例，则通常应优先于构造方法{@link #Byte（byte）}使用此方法，因为由于所有字节值都被缓存，该方法都可能产生明显更好的空间和时间性能。
     * 注意：ByteCache包含了所有byte可能值的Byte对象，直接从ByteCache的数组中获取对应的Byte对象即可。
     * @param b
     * @return
     */
    public static Byte valueOf(byte b) {
        final int offset = 128;
        //下标0保存的是-128，所以传入的值需要+128来确定下标
        return ByteCache.cache[(int)b + offset];
    }

    /**
     * Parses the string argument as a signed {@code byte} in the
     * radix specified by the second argument. The characters in the
     * string must all be digits, of the specified radix (as
     * determined by whether {@link java.lang.Character#digit(char,
     * int)} returns a nonnegative value) except that the first
     * character may be an ASCII minus sign {@code '-'}
     * ({@code '\u005Cu002D'}) to indicate a negative value or an
     * ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to
     * indicate a positive value.  The resulting {@code byte} value is
     * returned.
     *
     * <p>An exception of type {@code NumberFormatException} is
     * thrown if any of the following situations occurs:
     * <ul>
     * <li> The first argument is {@code null} or is a string of
     * length zero.
     *
     * <li> The radix is either smaller than {@link
     * java.lang.Character#MIN_RADIX} or larger than {@link
     * java.lang.Character#MAX_RADIX}.
     *
     * <li> Any character of the string is not a digit of the
     * specified radix, except that the first character may be a minus
     * sign {@code '-'} ({@code '\u005Cu002D'}) or plus sign
     * {@code '+'} ({@code '\u005Cu002B'}) provided that the
     * string is longer than length 1.
     *
     * <li> The value represented by the string is not a value of type
     * {@code byte}.
     * </ul>
     *
     * @param s         the {@code String} containing the
     *                  {@code byte}
     *                  representation to be parsed
     * @param radix     the radix to be used while parsing {@code s}
     * @return          the {@code byte} value represented by the string
     *                   argument in the specified radix
     * @throws          NumberFormatException If the string does
     *                  not contain a parsable {@code byte}.
     */
    /**
     * 1、将字符串参数解析为第二个参数指定的进制中的带符号{@code byte}。
     *   字符串中的字符必须全部为指定进制的数字（由{@link java.lang.Character＃digit（char，int）}是否返回非负值确定），但第一个字符可以是ASCII减号 符号{@code'-'}（{@code'\ u005Cu002D'}）表示负值，或ASCII加号{@code'+'}（{@code'\ u005Cu002B'}）表示正值 。
     *   返回结果{@code byte}的值。
     * 2、如果发生以下任何情况，将引发类型为{@code NumberFormatException}的异常：
     *   a、第一个参数为{@code null}或长度为零的字符串。
     *   b、进制数小于{@link java.lang.Character＃MIN_RADIX}或大于{@link java.lang.Character＃MAX_RADIX}。
     *   c、字符串的任何字符都不是指定进制的数字，除了第一个字符可以是减号{@code'-'}（{@code'\ u005Cu002D'}）或加号{@code'+' }（{@code'\ u005Cu002B'}），前提是该字符串的长度大于长度1。
     *   d、字符串表示的值不是{@code byte}类型的值。
     * @param s  待转换的字符串
     * @param radix  进制数
     * @return  表示字符串参数的指定进制的{@code byte}值
     * @throws NumberFormatException 如果字符串不包含可解析的{@code byte}。
     */
    public static byte parseByte(String s, int radix)
        throws NumberFormatException {
        //调用Integer的parseInt方法转换
        int i = Integer.parseInt(s, radix);
        //Integer的转换值再判断是不是在byte的最小值和最大值之间（范围为-128到127）
        if (i < MIN_VALUE || i > MAX_VALUE)
            //若不在范围内，则抛异常
            throw new NumberFormatException(
                "Value out of range. Value:\"" + s + "\" Radix:" + radix);

        //将int类型对象强转为byte类型对象
        return (byte)i;
    }

    /**
     * Parses the string argument as a signed decimal {@code
     * byte}. The characters in the string must all be decimal digits,
     * except that the first character may be an ASCII minus sign
     * {@code '-'} ({@code '\u005Cu002D'}) to indicate a negative
     * value or an ASCII plus sign {@code '+'}
     * ({@code '\u005Cu002B'}) to indicate a positive value. The
     * resulting {@code byte} value is returned, exactly as if the
     * argument and the radix 10 were given as arguments to the {@link
     * #parseByte(java.lang.String, int)} method.
     *
     * @param s         a {@code String} containing the
     *                  {@code byte} representation to be parsed
     * @return          the {@code byte} value represented by the
     *                  argument in decimal
     * @throws          NumberFormatException if the string does not
     *                  contain a parsable {@code byte}.
     */
    public static byte parseByte(String s) throws NumberFormatException {
        return parseByte(s, 10);
    }

    /**
     * Returns a {@code Byte} object holding the value
     * extracted from the specified {@code String} when parsed
     * with the radix given by the second argument. The first argument
     * is interpreted as representing a signed {@code byte} in
     * the radix specified by the second argument, exactly as if the
     * argument were given to the {@link #parseByte(java.lang.String,
     * int)} method. The result is a {@code Byte} object that
     * represents the {@code byte} value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s, radix))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @param radix     the radix to be used in interpreting {@code s}
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument in the
     *                  specified radix.
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    public static Byte valueOf(String s, int radix)
        throws NumberFormatException {
        return valueOf(parseByte(s, radix));
    }

    /**
     * Returns a {@code Byte} object holding the value
     * given by the specified {@code String}. The argument is
     * interpreted as representing a signed decimal {@code byte},
     * exactly as if the argument were given to the {@link
     * #parseByte(java.lang.String)} method. The result is a
     * {@code Byte} object that represents the {@code byte}
     * value specified by the string.
     *
     * <p> In other words, this method returns a {@code Byte} object
     * equal to the value of:
     *
     * <blockquote>
     * {@code new Byte(Byte.parseByte(s))}
     * </blockquote>
     *
     * @param s         the string to be parsed
     * @return          a {@code Byte} object holding the value
     *                  represented by the string argument
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code byte}.
     */
    public static Byte valueOf(String s) throws NumberFormatException {
        return valueOf(s, 10);
    }

    /**
     * Decodes a {@code String} into a {@code Byte}.
     * Accepts decimal, hexadecimal, and octal numbers given by
     * the following grammar:
     *
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     *
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
     *
     * <i>DecimalNumeral</i>, <i>HexDigits</i>, and <i>OctalDigits</i>
     * are as defined in section 3.10.1 of
     * <cite>The Java&trade; Language Specification</cite>,
     * except that underscores are not accepted between digits.
     *
     * <p>The sequence of characters following an optional
     * sign and/or radix specifier ("{@code 0x}", "{@code 0X}",
     * "{@code #}", or leading zero) is parsed as by the {@code
     * Byte.parseByte} method with the indicated radix (10, 16, or 8).
     * This sequence of characters must represent a positive value or
     * a {@link NumberFormatException} will be thrown.  The result is
     * negated if first character of the specified {@code String} is
     * the minus sign.  No whitespace characters are permitted in the
     * {@code String}.
     *
     * @param     nm the {@code String} to decode.
     * @return   a {@code Byte} object holding the {@code byte}
     *          value represented by {@code nm}
     * @throws  NumberFormatException  if the {@code String} does not
     *            contain a parsable {@code byte}.
     * @see java.lang.Byte#parseByte(java.lang.String, int)
     */
    /**
     * 1、将{@code String}解码为{@code Byte}。 接受以下语法给出的十进制、十六进制和八进制数字：
     *   a、解码字符串：以下面开头代表什么进制（demo如com.atlihao.jdk.base.one.ByteTest的测试例子2）
     *            十进制数字
     *      0x    十六进制数字
     *      0X    十六进制数字
     *      #     十六进制数字
     *      0     八进制数字
     *   b、符号：
     *      +
     *      -
     * @param nm     解码的字符串
     * @return   包含{@code nm}表示的{@code byte}值的{@code Byte}对象
     * @throws NumberFormatException  如果{@code String}不包含可解析的{@code byte}。
     */
    public static Byte decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        //Integer.decode的解码值,再判断是不是在byte的最小值和最大值之间（范围为-128到127）
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                    "Value " + i + " out of range from input " + nm);
        return valueOf((byte)i);
    }

    /**
     * The value of the {@code Byte}.
     *
     * @serial
     */
    /**
     * 一个变量来保存byte的值,被final修饰说明不可变
     */
    private final byte value;

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the specified {@code byte} value.
     *
     * @param value     the value to be represented by the
     *                  {@code Byte}.
     */
    /**
     * 构造一个新分配的{@code Byte}对象，该对象表示指定的{@code byte}值
     * @param value  由{@code Byte}表示的值
     */
    public Byte(byte value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Byte} object that
     * represents the {@code byte} value indicated by the
     * {@code String} parameter. The string is converted to a
     * {@code byte} value in exactly the manner used by the
     * {@code parseByte} method for radix 10.
     *
     * @param s         the {@code String} to be converted to a
     *                  {@code Byte}
     * @throws           NumberFormatException If the {@code String}
     *                  does not contain a parsable {@code byte}.
     * @see        java.lang.Byte#parseByte(java.lang.String, int)
     */
    /**
     * 1、构造一个新分配的{@code Byte}对象，该对象表示{@code String}参数指示的{@code byte}值。
     *   a、完全按照{@code parseByte}方法在十进制的时候的情况，将字符串转换为{@code byte}值。
     * @param s     {@code String}转换为{@code Byte}
     * @throws NumberFormatException  如果{@code String}不包含可解析的{@code byte}。
     */
    public Byte(String s) throws NumberFormatException {
        //默认转为十进制
        this.value = parseByte(s, 10);
    }

    /**
     * Returns the value of this {@code Byte} as a
     * {@code byte}.
     */
    public byte byteValue() {
        return value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code short} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public short shortValue() {
        return (short)value;
    }

    /**
     * Returns the value of this {@code Byte} as an {@code int} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    /**
     * 以{@code int}形式返回此{@code Byte}的值,在强制转换之后。
     * @return
     */
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code long} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    /**
     * 以{@code long}形式返回此{@code Byte}的值,在强制转换之后。
     * @return
     */
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code float} after
     * a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    /**
     * 以{@code float}形式返回此{@code Byte}的值,在强制转换之后。
     * @return
     */
    public float floatValue() {
        return (float)value;
    }

    /**
     * Returns the value of this {@code Byte} as a {@code double}
     * after a widening primitive conversion.
     * @jls 5.1.2 Widening Primitive Conversions
     */
    /**
     * 以{@code double}形式返回此{@code Byte}的值,在强制转换之后。
     * @return
     */
    public double doubleValue() {
        return (double)value;
    }

    /**
     * Returns a {@code String} object representing this
     * {@code Byte}'s value.  The value is converted to signed
     * decimal representation and returned as a string, exactly as if
     * the {@code byte} value were given as an argument to the
     * {@link java.lang.Byte#toString(byte)} method.
     *
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     */
    /**
     * 1、返回表示此{@code Byte}值的{@code String}对象。
     *   该值将转换为带符号的十进制表示形式，并以字符串形式返回，就像将{@code byte}值作为{@link java.lang.Byte＃toString（byte）}方法的参数给出一样。
     * @return 此对象的值（以十进制为基础）的字符串表示形式。
     */
    public String toString() {
        return Integer.toString((int)value);
    }

    /**
     * Returns a hash code for this {@code Byte}; equal to the result
     * of invoking {@code intValue()}.
     *
     * @return a hash code value for this {@code Byte}
     */
    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code byte} value; compatible with
     * {@code Byte.hashCode()}.
     *
     * @param value the value to hash
     * @return a hash code value for a {@code byte} value.
     * @since 1.8
     */
    /**
     * 返回一个{@code byte}值的哈希码
     * @param value  要进行hash的byte值
     * @return  {@code byte}值的哈希码值（也就是直接返回int类型的值）
     */
    public static int hashCode(byte value) {
        return (int)value;
    }

    /**
     * Compares this object to the specified object.  The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Byte} object that
     * contains the same {@code byte} value as this object.
     *
     * @param obj       the object to compare with
     * @return          {@code true} if the objects are the same;
     *                  {@code false} otherwise.
     */
    /**
     * 1、将此对象与指定对象进行比较。
     *   当且仅当参数不是{@code null}并且是一个包含与此对象相同的{@code byte}值的{@code Byte}对象时，结果为{@code true}。
     * @param obj   要比较的对象
     * @return  如果两个对象相同，则返回true；否则，返回false。
     */
    public boolean equals(Object obj) {
        //先判断是否都是Byte类型
        if (obj instanceof Byte) {
            //然后在比较值是否相同
            return value == ((Byte)obj).byteValue();
        }
        return false;
    }

    /**
     * Compares two {@code Byte} objects numerically.
     *
     * @param   anotherByte   the {@code Byte} to be compared.
     * @return  the value {@code 0} if this {@code Byte} is
     *          equal to the argument {@code Byte}; a value less than
     *          {@code 0} if this {@code Byte} is numerically less
     *          than the argument {@code Byte}; and a value greater than
     *           {@code 0} if this {@code Byte} is numerically
     *           greater than the argument {@code Byte} (signed
     *           comparison).
     * @since   1.2
     */
    public int compareTo(Byte anotherByte) {
        return compare(this.value, anotherByte.value);
    }

    /**
     * Compares two {@code byte} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Byte.valueOf(x).compareTo(Byte.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code byte} to compare
     * @param  y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    /**
     * 1、在数值上比较两个{@code byte}值。
     * @param x  要比较的第一个{@code byte}
     * @param y  要比较的第二个{@code byte}
     * @return   如果{@code x == y}，则值为{@code 0}；
     *           如果{@code x <y}，则该值小于{@code 0}；
     *           如果{@code x> y}，则该值大于{@code 0}。
     */
    public static int compare(byte x, byte y) {
        //通过相减来比较，大于0则说明x大于y。
        return x - y;
    }

    /**
     * Converts the argument to an {@code int} by an unsigned
     * conversion.  In an unsigned conversion to an {@code int}, the
     * high-order 24 bits of the {@code int} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code int} value and negative {@code
     * byte} values are mapped to an {@code int} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code int}
     * @return the argument converted to {@code int} by an unsigned
     *         conversion
     * @since 1.8
     */
    /**
     * 转成无符号int类型
     * @param x
     * @return
     */
    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    /**
     * Converts the argument to a {@code long} by an unsigned
     * conversion.  In an unsigned conversion to a {@code long}, the
     * high-order 56 bits of the {@code long} are zero and the
     * low-order 8 bits are equal to the bits of the {@code byte} argument.
     *
     * Consequently, zero and positive {@code byte} values are mapped
     * to a numerically equal {@code long} value and negative {@code
     * byte} values are mapped to a {@code long} value equal to the
     * input plus 2<sup>8</sup>.
     *
     * @param  x the value to convert to an unsigned {@code long}
     * @return the argument converted to {@code long} by an unsigned
     *         conversion
     * @since 1.8
     */
    /**
     * 1、转成无符号long类型
     *
     * 2、通过无符号将参数转换为{@code long}转换。
     *    在无符号转换为{@code long}的过程中，{@ code long}的高56位为零，而低8位等于{@code byte}参数的位
     * 3、因此，零个和正的{@code byte}值映射到一个数值相等的{@code long}值
     *    负的{@code byte}值映射到一个等于输入值加2的8次方的{@code long}值 。
     *
     * @param x  要转换为无符号{@code long}的值
     * @return  通过无符号转换将参数转换为{@code long}
     */
    public static long toUnsignedLong(byte x) {
        return ((long) x) & 0xffL;
    }


    /**
     * The number of bits used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.5
     */
    /**
     * 用来表示二进制补码形式{@code byte}值（以两位为单位）的比特数(也就是位数)
     */
    public static final int SIZE = 8;

    /**
     * The number of bytes used to represent a {@code byte} value in two's
     * complement binary form.
     *
     * @since 1.8
     */
    /**
     * 用于表示二进制补码形式的{@code byte}值的字节数。
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = -7183698231559129828L;
}
