package com.indower.ans.models.docModels;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.indower.utils.AppConstants;

import jakarta.validation.constraints.NotNull;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static java.nio.charset.StandardCharsets.UTF_8;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "ans")
public class ANS {

    private String _id;

    // it's not fetched for response
    private String doerId;

    @JsonProperty(value = "toWhomId", required = true)
    @NotNull(message = "Please mention toWhomId")
    private String toWhomId;

    @JsonProperty(value = "text", required = true)
    @NotNull(message = "Please give text")
    private String text;

    // @JsonProperty(value = "isAbusive", required = true)
    // @NotNull(message = "Please predict comment first")
    // private Boolean isAbusive;

    // @JsonProperty(value = "predictionResult", required = true)
    // @NotNull(message = "Please predict comment first")
    // private Float predictionResult;

    private Boolean isShow = true;

    private Boolean isAbusive = false;

    // -1 -> No reaction
    // 1 -> helpful
    private Integer userReaction = -1;

    // -1 -> not predicted
    // 0 -> Abusive (AB)
    // 1 -> Not Abusive (NA)
    // 2 -> Unknown Language (UN)
    private Integer predictionStatus = -1;

    private Integer ansStatus = -1;

    private String encryptionKey = null;
    private String encryptionCountry = null;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    // doer id will not visible
    public String getDoerId() {
        return null;
    }

    public void setDoerId(String doerId) {
        this.doerId = doerId;
    }

    public String getToWhomId() {
        return toWhomId;
    }

    public void setToWhomId(String toWhomId) {
        this.toWhomId = toWhomId;
    }

    public String getText() {
        if (this.encryptionCountry != null && this.encryptionKey != null && this.text != null) {
            String pass = AppConstants.makePasswordToEncodeDecode(getCreatedAt(), getEKey(), getECountry());
            return decrypt(this.text, pass);
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getPredictionStatus() {
        return predictionStatus;
    }

    public void setPredictionStatus(Integer predictionStatus) {
        this.predictionStatus = predictionStatus;
    }

    public Integer getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(Integer userReaction) {
        this.userReaction = userReaction;
    }

    public Boolean getIsAbusive() {
        return isAbusive;
    }

    public void setIsAbusive(Boolean isAbusive) {
        this.isAbusive = isAbusive;
    }

    public Integer getAnsStatus() {
        return ansStatus;
    }

    public void setAnsStatus(Integer ansStatus) {
        this.ansStatus = ansStatus;
    }

    public String getEKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String isEncrypted) {
        this.encryptionKey = isEncrypted;
    }

    public String getECountry() {
        return encryptionCountry;
    }

    public void setEncryptionCountry(String encryptionCountry) {
        this.encryptionCountry = encryptionCountry;
    }

    public void getEmptyTextANS() {
        this.setText(null);
    }

    private String decrypt(String text, String pass) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            Key key = new SecretKeySpec(messageDigest.digest(pass.getBytes(UTF_8)), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(DECRYPT_MODE, key);

            byte[] decoded = Base64.getDecoder().decode(text.getBytes(UTF_8));
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, UTF_8);

        } catch (Throwable e) {
            return null;
        }
    }

    public static int getAbusiveStatus(String abusivness) {
        switch (abusivness) {
            case "na":
                return 1;
            case "ab":
                return 0;
            default:
                return 2;
        }
    }

}
