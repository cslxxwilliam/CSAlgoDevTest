package models;

public class Fill {
    private double price;
    private int qty;
    private int seq;
    private static int seqCounter=0;
    public Fill(double filledPrice, int newMatchedQty, int seq) {

        this.price = filledPrice;
        this.qty = newMatchedQty;
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public static int nextSeq(){
        seqCounter++;
        return seqCounter;
    }
    @Override
    public String toString() {
        return
               formatNum(price) +
                "," + qty;
    }

    private String formatNum(double price) {
        if(price == (long) price)
            return String.format("%d",(long)price);
        else
            return String.format("%s",price);
    }
}
