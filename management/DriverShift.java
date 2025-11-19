import java.sql.Date;
import java.sql.Time;

public class DriverShift {
    private int driverId;
    private Date shiftDate;
    private Time startTime;
    private Time endTime;

    public DriverShift(int driverId, Date shiftDate, Time startTime, Time endTime) {
        this.driverId = driverId;
        this.shiftDate = shiftDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getDriverId() { return driverId; }
    public Date getShiftDate() { return shiftDate; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }

    @Override
    public String toString() {
        return "DriverShift{" +
                "driverId=" + driverId +
                ", shiftDate=" + shiftDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
