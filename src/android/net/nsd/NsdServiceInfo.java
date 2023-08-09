package android.net.nsd;

public class NsdServiceInfo {

    private String mServiceName;

    private String mServiceType;

    public void setServiceName(String serviceName) {
        mServiceName = serviceName;
    }
    public void setServiceType(String serviceType){}
    public void setPort(int port){

    }

    public String getServiceName() {
        return mServiceName;
    }
}
