package com.gloriane.smartwash.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gloriane.smartwash.dto.SensorReadingDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UplinkMessagePayload {

    @JsonProperty("frm_payload")
    private String frmPayload;

    @JsonProperty("decoded_payload")
    private SensorReadingDTO decodedPayload;

    public String getFrmPayload() { return frmPayload; }
    public void setFrmPayload(String frmPayload) { this.frmPayload = frmPayload; }

    public SensorReadingDTO getDecodedPayload() { return decodedPayload; }
    public void setDecodedPayload(SensorReadingDTO decodedPayload) { this.decodedPayload = decodedPayload; }
}
