
package com.ecosmart.ecosmart;

import jakarta.persistence.*;

@Entity
@Table(name = "smart_bins")
public class SmartBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String binName;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int fillPercentage;

    @Column(nullable = false)
    private String status;

    public SmartBin() {
    }

    public SmartBin(String binName, String location,
                    int fillPercentage, String status) {
        this.binName = binName;
        this.location = location;
        this.fillPercentage = fillPercentage;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getBinName() {
        return binName;
    }

    public String getLocation() {
        return location;
    }

    public int getFillPercentage() {
        return fillPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFillPercentage(int fillPercentage) {
        this.fillPercentage = fillPercentage;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

