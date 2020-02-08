/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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
package java.util;

/**
 * {@code StringJoiner} is used to construct a sequence of characters separated
 * by a delimiter and optionally starting with a supplied prefix
 * and ending with a supplied suffix.
 * <p>
 * Prior to adding something to the {@code StringJoiner}, its
 * {@code sj.toString()} method will, by default, return {@code prefix + suffix}.
 * However, if the {@code setEmptyValue} method is called, the {@code emptyValue}
 * supplied will be returned instead. This can be used, for example, when
 * creating a string using set notation to indicate an empty set, i.e.
 * <code>"{}"</code>, where the {@code prefix} is <code>"{"</code>, the
 * {@code suffix} is <code>"}"</code> and nothing has been added to the
 * {@code StringJoiner}.
 *
 * @apiNote
 * <p>The String {@code "[George:Sally:Fred]"} may be constructed as follows:
 *
 * <pre> {@code
 * StringJoiner sj = new StringJoiner(":", "[", "]");
 * sj.add("George").add("Sally").add("Fred");
 * String desiredString = sj.toString();
 * }</pre>
 * <p>
 * A {@code StringJoiner} may be employed to create formatted output from a
 * {@link java.util.stream.Stream} using
 * {@link java.util.stream.Collectors#joining(CharSequence)}. For example:
 *
 * <pre> {@code
 * List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
 * String commaSeparatedNumbers = numbers.stream()
 *     .map(i -> i.toString())
 *     .collect(Collectors.joining(", "));
 * }</pre>
 *
 * @see java.util.stream.Collectors#joining(CharSequence)
 * @see java.util.stream.Collectors#joining(CharSequence, CharSequence, CharSequence)
 * @since  1.8
*/

/**
 * 1、不可变类StringJoiner类
 * 2、StringJoiner用于构造由定界符分隔的字符序列，并可选地以提供的前缀开头和以提供的后缀结尾。
 * 3、在向StringJoiner添加内容之前，默认情况下，其 sj.toString()方法将返回prefix + suffix。
 *   但是，如果调用了setEmptyValue方法，则将返回提供的emptyValue。
 *   例如，在使用集合符号创建表示空集合的字符串（即" {}",其中前缀为" {" ，后缀}是"}"，并且未将任何内容添加到StringJoiner。
 * 4、这个字符串"[George:Sally:Fred]"可能被构造如下：
 *    StringJoiner sj = new StringJoiner(":", "[", "]");
 *    sj.add("George").add("Sally").add("Fred");
 *    String desiredString = sj.toString();
 * 5、可以使用StringJoiner来使用java.util.stream.Collectors＃joining（CharSequence）从java.util.stream.Stream}创建格式化的输出。
 *    例如：List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
 *        String commaSeparatedNumbers = numbers.stream()
 *                                      .map(i -> i.toString())
 *                                      .collect(Collectors.joining(", "));
 * @see java.util.stream.Collectors#joining(CharSequence)
 * @see java.util.stream.Collectors#joining(CharSequence, CharSequence, CharSequence)
 */
public final class StringJoiner {

    /**
     * 前缀
     */
    private final String prefix;

    /**
     * 分隔符   ---  也就是定界符
     */
    private final String delimiter;

    /**
     * 后缀
     */
    private final String suffix;

    /*
     * StringBuilder value -- at any time, the characters constructed from the
     * prefix, the added element separated by the delimiter, but without the
     * suffix, so that we can more easily add elements without having to jigger
     * the suffix each time.
     */
    /**
     * StringBuilder value-在任何时候，字符都是由前缀构造的，添加的元素由分隔符进行分隔，但是没有后缀，因此我们可以更轻松地添加元素，而不必每次都摇晃后缀。
     */
    private StringBuilder value;

    /*
     * By default, the string consisting of prefix+suffix, returned by
     * toString(), or properties of value, when no elements have yet been added,
     * i.e. when it is empty.  This may be overridden by the user to be some
     * other value including the empty String.
     */
    /**
     * 默认情况下，当尚未添加任何元素（即为空）时，由toString（）返回的由前缀+后缀组成的字符串或value的属性。
     * 用户可能会将其覆盖为其他一些值，包括空字符串。
     */
    private String emptyValue;

    /**
     * Constructs a {@code StringJoiner} with no characters in it, with no
     * {@code prefix} or {@code suffix}, and a copy of the supplied
     * {@code delimiter}.
     * If no characters are added to the {@code StringJoiner} and methods
     * accessing the value of it are invoked, it will not return a
     * {@code prefix} or {@code suffix} (or properties thereof) in the result,
     * unless {@code setEmptyValue} has first been called.
     *
     * @param  delimiter the sequence of characters to be used between each
     *         element added to the {@code StringJoiner} value
     * @throws NullPointerException if {@code delimiter} is {@code null}
     */
    /**
     * 1、构造一个StringJoiner，其中不包含任何字符，不包含前缀或后缀，并提供的分隔符的副本。
     * 2、如果没有字符添加到StringJoiner中，并且调用了访问value的方法，除非首先调用setEmptyValue()，否则它将不会在结果中返回前缀或后缀（或其属性）。
     * @param delimiter  添加到StringJoiner的每个元素之间要使用的字符序列
     * @throws NullPointerException  如果分隔符为null
     */
    public StringJoiner(CharSequence delimiter) {
        this(delimiter, "", "");
    }

    /**
     * Constructs a {@code StringJoiner} with no characters in it using copies
     * of the supplied {@code prefix}, {@code delimiter} and {@code suffix}.
     * If no characters are added to the {@code StringJoiner} and methods
     * accessing the string value of it are invoked, it will return the
     * {@code prefix + suffix} (or properties thereof) in the result, unless
     * {@code setEmptyValue} has first been called.
     *
     * @param  delimiter the sequence of characters to be used between each
     *         element added to the {@code StringJoiner}
     * @param  prefix the sequence of characters to be used at the beginning
     * @param  suffix the sequence of characters to be used at the end
     * @throws NullPointerException if {@code prefix}, {@code delimiter}, or
     *         {@code suffix} is {@code null}
     */
    /**
     * 1、使用提供的前缀、分隔符和后缀构造一个StringJoiner，其中不包含任何字符。
     * 2、如果没有字符添加到StringJoiner中，并且调用了访问其字符串的value的方法，则它将在结果中返回前缀+后缀(或其属性),除非首先调用setEmptyValue()。
     * @param delimiter  添加到StringJoiner的每个元素之间要使用的字符序列,就是分隔符
     * @param prefix  前缀
     * @param suffix  后缀
     * @throws NullPointerException 如果前缀、分隔符、后缀为空
     */
    public StringJoiner(CharSequence delimiter,
                        CharSequence prefix,
                        CharSequence suffix) {
        //校验前缀、分隔符、后缀不为空
        Objects.requireNonNull(prefix, "The prefix must not be null");
        Objects.requireNonNull(delimiter, "The delimiter must not be null");
        Objects.requireNonNull(suffix, "The suffix must not be null");
        // make defensive copies of arguments
        this.prefix = prefix.toString();
        this.delimiter = delimiter.toString();
        this.suffix = suffix.toString();
        //emptyValue为前缀+后缀
        this.emptyValue = this.prefix + this.suffix;
    }

    /**
     * Sets the sequence of characters to be used when determining the string
     * representation of this {@code StringJoiner} and no elements have been
     * added yet, that is, when it is empty.  A copy of the {@code emptyValue}
     * parameter is made for this purpose. Note that once an add method has been
     * called, the {@code StringJoiner} is no longer considered empty, even if
     * the element(s) added correspond to the empty {@code String}.
     *
     * @param  emptyValue the characters to return as the value of an empty
     *         {@code StringJoiner}
     * @return this {@code StringJoiner} itself so the calls may be chained
     * @throws NullPointerException when the {@code emptyValue} parameter is
     *         {@code null}
     */
    /**
     * 1、设置确定此StringJoiner的字符串表示形式时要使用的字符序列，并且尚未添加任何元素，即该元素为空时。
     *    为此目的，添加了emptyValue参数。
     *    请注意，一旦调用了add方法，即使添加的元素对应于空的String，StringJoiner也不再被认为是空的。
     * @param emptyValue  要返回的字符作为空StringJoiner的值
     * @return   StringJoiner本身，因此可以将调用链接在一起
     * @throws NullPointerException  emptyValue入参为null的话
     */
    public StringJoiner setEmptyValue(CharSequence emptyValue) {
        //校验emptyValue字符序列不为空
        this.emptyValue = Objects.requireNonNull(emptyValue,
            "The empty value must not be null").toString();
        return this;
    }

    /**
     * Returns the current value, consisting of the {@code prefix}, the values
     * added so far separated by the {@code delimiter}, and the {@code suffix},
     * unless no elements have been added in which case, the
     * {@code prefix + suffix} or the {@code emptyValue} characters are returned
     *
     * @return the string representation of this {@code StringJoiner}
     */
    /**
     * 1、返回当前值，该值由前缀、添加的值（由分隔符分隔）和后缀组成，除非在这种情况下未添加任何元素，则返回返回前缀+后缀或emptyValue字符
     * @return  此StringJoiner的字符串表示形式
     */
    @Override
    public String toString() {
        //若value为null，则返回emptyValue
        if (value == null) {
            return emptyValue;
        } else {
            //后缀为""，返回value
            if (suffix.equals("")) {
                return value.toString();
            } else {
                //获取value的长度
                int initialLength = value.length();
                //在value值后面增加后缀
                String result = value.append(suffix).toString();
                // reset value to pre-append initialLength
                //重置值以预先添加initialLength值
                value.setLength(initialLength);
                //返回结果字符串
                return result;
            }
        }
    }

    /**
     * Adds a copy of the given {@code CharSequence} value as the next
     * element of the {@code StringJoiner} value. If {@code newElement} is
     * {@code null}, then {@code "null"} is added.
     *
     * @param  newElement The element to add
     * @return a reference to this {@code StringJoiner}
     */
    /**
     * 1、将给定的CharSequence值的添加为StringJoiner值的下一个元素。
     *    如果newElement为null，则添加"null"。
     * @param newElement  要添加的元素
     * @return 对此StringJoiner的引用
     */
    public StringJoiner add(CharSequence newElement) {
        //追加新的元素到字符串
        prepareBuilder().append(newElement);
        return this;
    }

    /**
     * Adds the contents of the given {@code StringJoiner} without prefix and
     * suffix as the next element if it is non-empty. If the given {@code
     * StringJoiner} is empty, the call has no effect.
     *
     * <p>A {@code StringJoiner} is empty if {@link #add(CharSequence) add()}
     * has never been called, and if {@code merge()} has never been called
     * with a non-empty {@code StringJoiner} argument.
     *
     * <p>If the other {@code StringJoiner} is using a different delimiter,
     * then elements from the other {@code StringJoiner} are concatenated with
     * that delimiter and the result is appended to this {@code StringJoiner}
     * as a single element.
     *
     * @param other The {@code StringJoiner} whose contents should be merged
     *              into this one
     * @throws NullPointerException if the other {@code StringJoiner} is null
     * @return This {@code StringJoiner}
     */
    public StringJoiner merge(StringJoiner other) {
        Objects.requireNonNull(other);
        if (other.value != null) {
            final int length = other.value.length();
            // lock the length so that we can seize the data to be appended
            // before initiate copying to avoid interference, especially when
            // merge 'this'
            StringBuilder builder = prepareBuilder();
            builder.append(other.value, other.prefix.length(), length);
        }
        return this;
    }

    /**
     * 准备字符串建造方法
     * @return
     */
    private StringBuilder prepareBuilder() {
        //若value不为null，则追加分隔符，否则增加前缀
        if (value != null) {
            value.append(delimiter);
        } else {
            value = new StringBuilder().append(prefix);
        }
        return value;
    }

    /**
     * Returns the length of the {@code String} representation
     * of this {@code StringJoiner}. Note that if
     * no add methods have been called, then the length of the {@code String}
     * representation (either {@code prefix + suffix} or {@code emptyValue})
     * will be returned. The value should be equivalent to
     * {@code toString().length()}.
     *
     * @return the length of the current value of {@code StringJoiner}
     */
    /**
     * 1、返回此StringJoiner的String表示形式的长度。
     *   请注意，如果未调用任何add方法，则将返回String表示形式的长度（前缀+后缀或emptyValue）。
     *   该值应等效于toString().length()。
     * @return  StringJoiner当前value的长度
     */
    public int length() {
        // Remember that we never actually append the suffix unless we return
        // the full (present) value or some sub-string or length of it, so that
        // we can add on more if we need to.
        // 请记住，除非返回，否则我们从不实际附加后缀
        // 完整（当前）值或它的某些子字符串或长度，以便如果需要，我们可以添加更多。
        //a、如果value不为空，则长度为value的长度+后缀的长度
        //b、如果value为空，则返回emptyValue的长度
        return (value != null ? value.length() + suffix.length() :
                emptyValue.length());
    }
}
