/*Copyright 2016 WorkStress Experiment
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package uk.ac.mdx.cs.ie.acontextlib;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;

import java.util.UUID;

/**
 * Heartrate Monitor
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class HeartRateMonitor extends BluetoothLEDevice {

    public final static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public final static String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";

    public HeartRateMonitor(Context c, ContextReceiver cr) {
        super(c, cr, UUID.fromString(HEART_RATE_SERVICE), UUID.fromString(HEART_RATE_MEASUREMENT));
    }

    public void checkContext(BluetoothGattCharacteristic characteristic) {

        int flag = characteristic.getProperties();
        int format = -1;

        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
        }

        final int heartRate = characteristic.getIntValue(format, 1);

        mReceiver.newContextValue("sensor.heartrate", heartRate);
    }

    @Override
    public void connectionChange(boolean connected) {
        mReceiver.newContextValue("sensor.heartrate.connected", connected);
    }
}
