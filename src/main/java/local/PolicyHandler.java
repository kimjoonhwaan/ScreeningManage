package local;

import local.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    ScreeningRepository screeningRepository;
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverHospitalDeleted_ForceCancel(@Payload HospitalDeleted hospitalDeleted){

        if(hospitalDeleted.isMe()){
            System.out.println("##### listener ForceCancel : " + hospitalDeleted.toJson());
            List<Screening> list = screeningRepository.findByHospitalId(hospitalDeleted.getId());
            for(Screening temp : list){
                // 취소된것은 뺄까 ? 굳이 로직까지?
                //if("Canceled".equals(temp.getStatus()))
                temp.setStatus("ForceCanceled");
                screeningRepository.save(temp);
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCompleted_UpdatedStatus(@Payload ReservationCompleted reservationCompleted){

        if(reservationCompleted.isMe()){
            System.out.println("##### listener UpdatedStatus : " + reservationCompleted.toJson());

            Optional<Screening> temp = screeningRepository.findById(reservationCompleted.getScreeningId());
            Screening target = temp.get();
            target.setStatus(reservationCompleted.getStatus());
            screeningRepository.save(target);
        }
    }

}
