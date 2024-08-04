package serverRfid.model.constant;

public enum ReadTagBankEnum {
    BANK_RESERVED(0),
    BANK_EPC(1),
    BANK_TID(2),
    BANK_USER(3);
    private final int value;
    public final int getValue() {
        return this.value;
    }
    private ReadTagBankEnum(int num) {
        this.value = num;
    }
}
