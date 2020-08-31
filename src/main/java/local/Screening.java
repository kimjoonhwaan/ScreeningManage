package local;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Screening_table")
public class Screening {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long hospitalId;
    private String chkDate;
    private String custNm;
    private String status;
    private String hospitalNm;

    @PrePersist
    public void onPrePersist(){
        Requested requested = new Requested();
        BeanUtils.copyProperties(this, requested);
        //requested.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        // 검진 요청함 ( Req / Res : 동기 방식 호출)
        local.external.Hospital hospital = new local.external.Hospital();
        hospital.setHospitalId(requested.getHospitalId());
        // mappings goes here
        ScreeningMangeApplication.applicationContext.getBean(local.external.HospitalService.class)
            .screeningRequest(hospital.getHospitalId(),hospital);
    }
    @PostPersist
    public void onPostPersist(){
        Requested requested = new Requested();
        BeanUtils.copyProperties(this, requested);
        requested.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){

        System.out.println("#### onPostUpdate :" + this.toString());

        if("Canceled".equals(this.getStatus())) {
            Canceled canceled = new Canceled();
            BeanUtils.copyProperties(this, canceled);
            canceled.publishAfterCommit();
        }

/*
        ForceCanceled forceCanceled = new ForceCanceled();
        BeanUtils.copyProperties(this, forceCanceled);
        forceCanceled.publishAfterCommit();
*/
    }

    @PreRemove
    public void onPreRemove(){
        HospitalDeleted hospitalDeleted = new HospitalDeleted();
        BeanUtils.copyProperties(this, hospitalDeleted);
        hospitalDeleted.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getChkDate() {
        return chkDate;
    }

    public void setChkDate(String chkDate) {
        this.chkDate = chkDate;
    }
    public String getCustNm() {
        return custNm;
    }

    public void setCustNm(String custNm) {
        this.custNm = custNm;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getHospitalNm() {
        return hospitalNm;
    }

    public void setHospitalNm(String hospitalNm) {
        this.hospitalNm = hospitalNm;
    }




}
