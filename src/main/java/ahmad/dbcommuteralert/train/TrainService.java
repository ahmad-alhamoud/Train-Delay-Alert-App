package ahmad.dbcommuteralert.train;

import org.springframework.stereotype.Service;

@Service
public class TrainService {

    public TrainStatusInfo checkTrainStatus(String trainNumber) {
        boolean isDelayed = true;
        int delayMinutes = 15;

        return new TrainStatusInfo(trainNumber, isDelayed, delayMinutes);
    }
}
