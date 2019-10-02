package xyz.frankk.dorm;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DormBean {
    private Integer price;
    private String info;

    public DormBean(){
    }

    public DormBean(Integer k,String  v){
        this.price = k;
        this.info = v;
    }
}
