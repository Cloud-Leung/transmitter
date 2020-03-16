package cn.transmitter.aggregate.lock;

/**
 *
 * @author cloud
 */
public class AggregateLockResult {

    private boolean success;

    private String key;

    private String lockValue;

    public AggregateLockResult(boolean success, String key, String lockValue) {
        this.success = success;
        this.lockValue = lockValue;
        this.key = key;
    }



    public boolean isSuccess() {
        return success;
    }

    public String getLockValue() {
        return lockValue;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "AggregateLockResult{" + "success=" + success + ", key='" + key + '\'' + ", lockValue='" + lockValue +
               '\'' + '}';
    }
}
