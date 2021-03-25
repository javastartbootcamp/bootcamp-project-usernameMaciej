package pl.javastart.bootcamp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("javastart")
public class JavaStartProperties {

    private String fullDomainAddress;
    private String agreementsDir;
    private String filesDir;
    private boolean googleCalendarSyncEnabled;
    private String sendboxHost;
    private String sendboxUsername;
    private String sendboxPassword;

    public void setFullDomainAddress(String fullDomainAddress) {
        this.fullDomainAddress = fullDomainAddress;
    }

    public String getFullDomainAddress() {
        return fullDomainAddress;
    }

    public String getAgreementsDir() {
        return agreementsDir;
    }

    public String getFilesDir() {
        return filesDir;
    }

    public void setFilesDir(String filesDir) {
        this.filesDir = filesDir;
    }

    public void setAgreementsDir(String agreementsDir) {
        this.agreementsDir = agreementsDir;
    }

    public boolean isGoogleCalendarSyncEnabled() {
        return googleCalendarSyncEnabled;
    }

    public void setGoogleCalendarSyncEnabled(boolean googleCalendarSyncEnabled) {
        this.googleCalendarSyncEnabled = googleCalendarSyncEnabled;
    }

    public String getSendboxHost() {
        return sendboxHost;
    }

    public void setSendboxHost(String sendboxHost) {
        this.sendboxHost = sendboxHost;
    }

    public String getSendboxUsername() {
        return sendboxUsername;
    }

    public void setSendboxUsername(String sendboxUsername) {
        this.sendboxUsername = sendboxUsername;
    }

    public String getSendboxPassword() {
        return sendboxPassword;
    }

    public void setSendboxPassword(String sendboxPassword) {
        this.sendboxPassword = sendboxPassword;
    }
}