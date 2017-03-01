package com.log.jsq.library;

import android.content.Context;
import android.util.SparseArray;

import com.log.jsq.R;

public class Nums {
    public static String[] nums = null;
    public static final int NO_FOUND = -1;
    public static final int FU = -2;
    public static final int ZHENG = -3;
    public static final int DIAN = -4;
    public static final int TEN_POWER = -5;
    public static final int TEN_POWER_END = -6;
    public static final int ZH_SHI = 14;
    public static final int ZH_BAI = 15;
    public static final int ZH_QIAN = 16;
    public static final int ZH_WAN = 17;
    public static final int ZH_YI = 18;
    public static final int[] ZH_LIST = new int[] {ZH_SHI, ZH_BAI, ZH_QIAN, ZH_WAN, ZH_SHI, ZH_BAI, ZH_QIAN, ZH_YI, ZH_SHI, ZH_BAI, ZH_QIAN, ZH_WAN};

    public static class CapsZH {
        private final static int ZH_ZHENG = 10;
        private final static int ZH_FEN = 11;
        private final static int ZH_JIAO = 12;
        private final static int ZH_YUAN = 13;
        private static SparseArray<String> sparseArray;

        private static void initialize (Context context) {
            String[] capsNum = context.getResources().getStringArray(R.array.capsNum);
            CapsZH.sparseArray = new SparseArray<>(19);

            for (int i=0;i<capsNum.length;i++) {
                sparseArray.put(i, capsNum[i]);
            }

            sparseArray.put(FU, context.getString(R.string.capsFu));
            sparseArray.put(ZH_ZHENG, context.getString(R.string.capsZheng));
            sparseArray.put(ZH_FEN, context.getString(R.string.capsFen));
            sparseArray.put(ZH_JIAO, context.getString(R.string.capsJiao));
            sparseArray.put(ZH_YUAN, context.getString(R.string.capsYuan));
            sparseArray.put(ZH_SHI, context.getString(R.string.capsShi));
            sparseArray.put(ZH_BAI, context.getString(R.string.capsBai));
            sparseArray.put(ZH_QIAN, context.getString(R.string.capsQian));
            sparseArray.put(ZH_WAN, context.getString(R.string.capsWan));
            sparseArray.put(ZH_YI, context.getString(R.string.capsYi));
        }

        public static String toCapsZH(String numStr, Context context) {
            StringBuffer strBuf = new StringBuffer();

            if (CapsZH.sparseArray == null) {
                initialize(context);
            }

            int[] intArray;
            boolean lengthOutBounds = true;
            final int dianIndex = numStr.indexOf(FuHao.dian);

            if (dianIndex == -1) {
                intArray = toIntArray(numStr, true);
            } else {
                intArray = toIntArray(numStr.substring(0, dianIndex), true);
            }

            intArray[intArray.length-1] = ZH_YUAN;
            if (intArray.length <= 2) {
                lengthOutBounds = false;
            }

            if (!(intArray.length == 2 && intArray[0] == 0 && intArray[1] == ZH_YUAN)) {
                for (int anIntArray : intArray) {
                    String str = sparseArray.get(anIntArray);

                    if (str != null) {
                        strBuf.append(str);
                    }

                    if (anIntArray >= ZH_SHI && anIntArray <= ZH_YI) {
                        lengthOutBounds = false;
                    }
                }
            }

            if (lengthOutBounds) {
                return null;
            } else {
                if (dianIndex > 0) {
                    // 识别“角”、“分”
                    int[] appendIntArray = new int[3];
                    int appendLen = 0;
                    String appendStr = numStr.substring(dianIndex + FuHao.dian.length());

                    for (int i=0, index=0;i<appendIntArray.length;i++) {
                        int num = onStartNum(appendStr, index);
                        appendIntArray[i] = num;
                        index += sparseArray.get(num).length();
                        appendLen += 1;

                        if (index >= appendStr.length()) {
                            if (i == appendIntArray.length-1 && num >= 5) {
                                if (intArray[0] == FU) {
                                    appendIntArray[i - 1] -= 1;
                                } else {
                                    appendIntArray[i - 1] += 1;
                                }
                                appendLen -= 1;
                            }
                            break;
                        }
                    }

                    if ((appendLen > 1 && strBuf.length() > 0) || appendIntArray[0] != 0) {
                        strBuf.append(sparseArray.get(appendIntArray[0]));
                    }
                    if (appendIntArray[0] != 0) {
                        strBuf.append(sparseArray.get(ZH_JIAO));
                    }
                    if (appendLen >= 2 && appendIntArray[1] != 0) {
                        strBuf.append(sparseArray.get(appendIntArray[1]));
                        strBuf.append(sparseArray.get(ZH_FEN));
                    }
                } else {
                    strBuf.append(sparseArray.get(ZH_ZHENG));
                }

                return strBuf.toString();
            }
        }
    }

    public static void luRu(Context context) {
        nums = new String[] {
                context.getString(R.string._0),
                context.getString(R.string._1),
                context.getString(R.string._2),
                context.getString(R.string._3),
                context.getString(R.string._4),
                context.getString(R.string._5),
                context.getString(R.string._6),
                context.getString(R.string._7),
                context.getString(R.string._8),
                context.getString(R.string._9),
        };
    }

    public static boolean isNum(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        for (String num:nums) {
            if (str.equals(num)) {
                return true;
            }
        }

        return false;
    }

    public static int onStartNum(String str, int startIndex) {
        for (int i=0;i<nums.length;i++) {
            if (str.startsWith(nums[i], startIndex)) {
                return i;
            }
        }

        if (str.startsWith(FuHao.jian, startIndex)) {
            return FU;
        } else if (str.startsWith(FuHao.jia, startIndex)) {
            return ZHENG;
        } else if (str.startsWith(FuHao.dian, startIndex)) {
            return DIAN;
        } else if (str.startsWith(FuHao.TEN_POWER, startIndex)) {
            return TEN_POWER;
        }

        return NO_FOUND;
    }

    public static int[] toIntArray(String str, boolean isRMB) {
        int[] array = new int[str.length() + 1];

        for (int i=0;i<array.length-1;i++) {
            int value = onStartNum(str, i);

            if (value == NO_FOUND) {
                return  new int[] {};
            } else {
                array[i] = value;
            }
        }

        if (str.contains(FuHao.TEN_POWER)) {
            array[array.length - 1] = TEN_POWER_END;
        } else {
            array[array.length - 1] = NO_FOUND;
        }

        for (int i=0;i<array.length;i++) {
            if (isNum(array[i])) {
                int endIndex = 0;

                for (int j=i;j<array.length;j++) {
                    if (!isNum(array[j])) {
                        endIndex = j;
                        break;
                    }
                }

                if (i>1 && array[i-1] == DIAN) {
                    i = endIndex - 1;
                } else if (endIndex > 0 && endIndex-i>1) {
                    int[] addArray = addZH(array, i, endIndex, isRMB);
                    int addLen = addArray.length;
                    int[] newArray = new int[array.length - (endIndex-i) + addLen];
                    System.arraycopy(array, 0, newArray, 0, i);
                    System.arraycopy(addArray, 0, newArray, i, addLen);
                    System.arraycopy(array, endIndex, newArray, i + addLen, array.length - endIndex);
                    i += addLen;
                    array = newArray;
                }
            }
        }

        return array;
    }

    private static int[] addZH(int[] array, final int intStartIndex, final int intEndIndex, boolean isRMB) {
        for (int i=intStartIndex;i < intEndIndex;i++) {
            if (!isNum(array[i])) {
                throw new ArrayIndexOutOfBoundsException(array[i] + " is not num!" + "  intStartIndex = " + intStartIndex + ", intEndIndex = " + intEndIndex + ", index = " + i);
            }
        }

        final int intLen = intEndIndex - intStartIndex;
        if (intLen > ZH_LIST.length + 1) {
            return array;
        } else {
            int addLen = intLen - 1;
            int[] buffArray = new int[intLen + addLen];
            System.arraycopy(array, intStartIndex, buffArray, 0, intLen);

            for (int i=0;i<buffArray.length-1 && isNum(buffArray[i+1]);i += 2) {
                System.arraycopy(buffArray, i+1, buffArray, i+2, addLen);
                buffArray[i+1] = ZH_LIST[addLen-1];
                addLen--;
            }

            return simplifyIntArray(buffArray, isRMB);
        }
    }

    private static boolean isNum(int i) {
        return i>=0 && i<=9;
    }

    private static int[] simplifyIntArray(int[] array, boolean isRMB) {
        int arrayLen = array.length;

        for (int i=0; i < arrayLen; i++) {
            switch (array[i]) {
                //删除“0”后面的位
                case 0:
                    if (i+1 < array.length && array[i+1] != ZH_YI && array[i+1] != ZH_WAN && !isNum(array[i+1])) {
                        System.arraycopy(array, i+2, array, i+1, arrayLen-i-2);
                        arrayLen -= 1;
                    }
                    break;

                //删除“十”前面的“1”
                case 1:
                    if (!isRMB && i<array.length-1 && array[i+1] == ZH_SHI) {
                        if (i == 0 || (i-1 >= 0 && (array[i-1] == FU || array[i-1] == ZHENG || array[i-1] == TEN_POWER))) {
                            System.arraycopy(array, i + 1, array, i, arrayLen - i - 1);
                            arrayLen -= 1;
                        }
                    }
                    break;
            }
        }

        //删除 亿、万位 后面多余的“0”，只保留一个“0”
        for (int i=0; i<arrayLen; i++) {
            if (array[i] == ZH_YI || array[i] == ZH_WAN) {
                int delLen = 0;

                for (int j=i+1;j<arrayLen;j++) {
                    if (array[j] == 0) {
                        delLen += 1;
                    } else {
                        break;
                    }
                }

                if (delLen > 1) {
                    System.arraycopy(array, i+delLen, array, i+1, arrayLen-i-(delLen-1));
                    arrayLen -= delLen-1;
                }
            }
        }

        //删除 小位（如十、百、千位等） 后面到 亿、万、个位 之间的“0”
        for (int i=arrayLen;i>=0;i--) {
            if (i == arrayLen || array[i] == ZH_YI || array[i] == ZH_WAN) {
                int delLen = 0;

                for (int j=i-1;j >= 0;j--) {
                    if (array[j] == 0) {
                        delLen += 1;
                    } else {
                        break;
                    }
                }

                if (delLen > 0) {
                    System.arraycopy(array, i, array, i-delLen, arrayLen-i);
                    arrayLen -= delLen;
                }
            }
        }

        int[] newArray = new int[arrayLen];
        System.arraycopy(array, 0, newArray, 0, arrayLen);
        return newArray;
    }

    public static String Ling(String numStr) {
        int dianIndex = numStr.indexOf(FuHao.dian);

        if (dianIndex >= 0) {
            int index = numStr.length() - 1;

            while (index > dianIndex) {
                if (numStr.lastIndexOf(nums[0], index) == index) {
                    index -= nums[0].length();
                } else {
                    break;
                }
            }

            if (index == dianIndex) {
                index -= FuHao.dian.length();
            }

            return numStr.substring(0, index + nums[0].length());
        }

        return numStr;
    }
}
