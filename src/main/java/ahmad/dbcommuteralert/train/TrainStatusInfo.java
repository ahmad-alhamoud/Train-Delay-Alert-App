package ahmad.dbcommuteralert.train;

public class TrainStatusInfo {

    private String trainNumber;
    private boolean isDelayed;
    private int delayMinutes;

    public TrainStatusInfo(String trainNumber, boolean isDelayed, int delayMinutes) {
        this.trainNumber = trainNumber;
        this.isDelayed = isDelayed;
        this.delayMinutes = delayMinutes;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }
}
