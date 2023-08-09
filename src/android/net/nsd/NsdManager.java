package android.net.nsd;

public class NsdManager {
    public static final int PROTOCOL_DNS_SD = 0x0001;

    public interface RegistrationListener {
        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo);
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
        public void onServiceUnregistered(NsdServiceInfo arg0);
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode);
    }

    RegistrationListener listener;
    public void registerService(NsdServiceInfo serviceInfo, int protocolType, RegistrationListener listener) {
        this.listener = listener;
        listener.onServiceRegistered(serviceInfo);
    }

    public void unregisterService(RegistrationListener registrationListener) {
        listener.onServiceUnregistered(null);
    }
}
