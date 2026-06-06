package com.gloriane.smartwash.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TtnUplinkMessage {

    @JsonProperty("end_device_ids")
    private EndDeviceIds endDeviceIds;

    @JsonProperty("received_at")
    private String receivedAt;

    @JsonProperty("uplink_message")
    private UplinkMessagePayload uplinkMessage;

    // Getters and Setters
    public EndDeviceIds getEndDeviceIds() { return endDeviceIds; }
    public void setEndDeviceIds(EndDeviceIds endDeviceIds) {
        this.endDeviceIds = endDeviceIds;
    }

    public String getReceivedAt() { return receivedAt; }
    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public UplinkMessagePayload getUplinkMessage() {
        return uplinkMessage;
    }
    public void setUplinkMessage(UplinkMessagePayload uplinkMessage) {
        this.uplinkMessage = uplinkMessage;
    }
}
