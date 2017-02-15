package com.log.jsq.library;

public class Nums {
    public static String[] nums = null;
    public static final int NO_FOUND = -1;
    public static final int FU = -2;
    public static final int ZHENG = -3;
    public static final int DIAN = -4;
    public static final int TEN_POWER = -5;
    public static final int TEN_POWER_END = -6;
    public static final int ZH_SHI = 11;
    public static final int ZH_BAI = 12;
    public static final int ZH_QIAN = 13;
    public static final int ZH_WAN = 14;
    public static final int ZH_YI = 15;
    public static final int[] ZH_LIST = new int[] {ZH_SHI, ZH_BAI, ZH_QIAN, ZH_WAN, ZH_SHI, ZH_BAI, ZH_QIAN, ZH_YI, ZH_SHI, ZH_BAI, ZH_QIAN, ZH_WAN};

    public static void luRu(String n0, String n1, String n2, String n3, String n4, String n5, String n6, String n7, String n8, String n9){
        nums = new String[10];

        nums[0] = n0;
        nums[1] = n1;
        nums[2] = n2;
        nums[3] = n3;
        nums[4] = n4;
        nums[5] = n5;
        nums[6] = n6;
        nums[7] = n7;
        nums[8] = n8;
        nums[9] = n9;
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
//        str.substring(startIndex, str.length());
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

    public static int[] toIntArray(String str) {
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
                    int[] addArray = addZH(array, i, endIndex);
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

    private static int[] addZH(int[] array, final int intStartIndex, final int intEndIndex) {
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

            return simplifyIntArray(buffArray);
        }
    }

    private static boolean isNum(int i) {
        return i>=0 && i<=9;
    }

    private static int[] simplifyIntArray(int[] array) {
        int arrayLen = array.length;

        for (int i=0; i < arrayLen; i++) {
            switch (array[i]) {
                //删除“0”后面的位
                case 0:
                    if (array[i+1] != ZH_YI && array[i+1] != ZH_WAN && !isNum(array[i+1])) {
                        System.arraycopy(array, i+2, array, i+1, arrayLen-i-2);
                        arrayLen -= 1;
                    }
                    break;

                //删除“十”前面的“1”
                case 1:
                    if (i<array.length-1 && array[i+1] == ZH_SHI) {
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
}
