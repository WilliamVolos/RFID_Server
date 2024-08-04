package serverRfid.model.constant;

public enum AntennaEnum {
    ANT1(1),
    ANT2(2),
    ANT3(3),
    ANT4(4),
    ANT5(5),
    ANT6(6),
    ANT7(7),
    ANT8(8),
    ANT9(9),
    ANT10(10),
    ANT11(11),
    ANT12(12),
    ANT13(13),
    ANT14(14),
    ANT15(15),
    ANT16(16);

    private final int value;

    public final int getValue() {
        return this.value;
    }

    public static AntennaEnum getValue(int numberAnt) {
        switch (numberAnt) {
            case 1:
                return ANT1;
            case 2:
                return ANT2;
            case 3:
                return ANT3;
            case 4:
                return ANT4;
            case 5:
                return ANT5;
            case 6:
                return ANT6;
            case 7:
                return ANT7;
            case 8:
                return ANT8;
            case 9:
                return ANT9;
            case 10:
                return ANT10;
            case 11:
                return ANT11;
            case 12:
                return ANT12;
            case 13:
                return ANT13;
            case 14:
                return ANT14;
            case 15:
                return ANT15;
            case 16:
                return ANT16;
            default:
                return null;
        }
    }

    private AntennaEnum(int numberAnt) {
        this.value = numberAnt;
    }
}
