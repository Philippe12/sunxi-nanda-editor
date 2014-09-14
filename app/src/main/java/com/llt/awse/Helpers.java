/*
 * Copyright 2013 Bartosz Jankowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.llt.awse;

import android.os.Environment;
import android.util.Log;

import com.spazedog.lib.rootfw3.RootFW;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers
{
    final private static String TAG = "AWSE";
	final private static Pattern pRegEx = Pattern.compile("^port:(.*)<(.*)><(.*)><(.*)><(.*)>$");


    // List of all known to me sections
    // Used to get config on boot 2.0 devices
    // Source: http://linux-sunxi.org/Fex_Guide
    public final static String[] mScriptSections = {"3g_para", "audio_para", "bt_para", "can_para", "card0_boot_para",
            "card2_boot_para", "card_boot0_para", "card_boot2_para", "card_boot", "card_burn_para", "clock",
            "compass_para", "cpus_config_paras", "csi0_para", "csi1_para", "ctp_list_para", "ctp_para", "disp_init",
            "dram_para", "dvfs_table", "dynamic", "emac_para", "fel_key", "g2d_para", "gmac_para", "gpio_init", "gpio_para",
            "gps_para", "gsensor_para", "gy_para", "hdmi_para", "i2s_para", "ir_para", "jtag_para", "lcd0_para", "lcd1_para",
            "leds_para", "locks_para", "ls_para", "mali_para", "mmc0_para", "mmc1_para", "mmc2_para", "mmc3_para", "motor_para",
            "ms_para", "msc_feature", "nand0_para", "nand1_para", "nand_para", "pcm_para", "platform", "pmu_para", "power_sply",
            "product", "ps2_0_para", "ps2_1_para", "recovery_key", "rtp_para", "sata_para", "sdio_wifi_para", "smc_para",
            "spdif_para", "spi0_para", "spi1_para", "spi2_para", "spi3_para", "spi_board0", "spi_devices", "system",
            "tabletkeys_para", "target", "tkey_para", "tv_out_dac_para", "tvin_para", "tvout_para", "twi0_para", "twi1_para",
            "twi2_para", "twi3_para", "twi_para", "uart_para0", "uart_para1", "uart_para2", "uart_para3", "uart_para4",
            "uart_para5", "uart_para6", "uart_para7", "uart_para", "usb_feature", "usb_wifi_para", "usbc0", "usbc1", "usbc2",
            "vip0_para", "vip1_para", "wifi_para"};

    static String[] getScriptSection(final RootFW root, final String section)
    {
        /*
        ++++++++++++++++++++++++++__sysfs_dump_mainkey++++++++++++++++++++++++++
    name:      wifi_para
    sub_key:   name           type      value
               ap6xxx_wl_regongpio      (gpio: 186, mul: 1, pull -1, drv -1, data 0)
               ap6xxx_wl_host_wakegpio      (gpio: 187, mul: 0, pull -1, drv -1, data 0)
               ap6xxx_bt_regongpio      (gpio: 189, mul: 1, pull -1, drv -1, data 0)
               ap6xxx_bt_wake gpio      (gpio: 139, mul: 1, pull -1, drv -1, data 0)
               ap6xxx_bt_host_wakegpio      (gpio: 188, mul: 0, pull -1, drv -1, data 0)
               wifi_power     string    "axp22_dldo1"
               wifi_used      int       1
               wifi_sdc_id    int       1
               wifi_usbc_id   int       1
               wifi_usbc_type int       1
               wifi_mod_sel   int       2
               ap6xxx_gpio_powerstring    "axp22_dldo2"
               ap6xxx_clk_powerstring    "axp22_dldo4"
--------------------------__sysfs_dump_mainkey--------------------------
         */
        return null;
    }

	static boolean isPortEntry(String val)
	{
		return pRegEx.matcher(val).find();
	}
	
	static String[] getPortValues(String val)
	{
		Matcher m = pRegEx.matcher(val);
		String[] ret = new String[5];
		if(m.find())
		{
			ret[0] = m.group(1);
			ret[1] = m.group(2);
			ret[2] = m.group(3);
			ret[3] = m.group(4);
			ret[4] = m.group(5);
			
		}
		return ret;
	}

    static void unmountLoader(final RootFW root)
    {
        if (root.filesystem("/dev/block/nanda").isMounted()) {
            Log.v(TAG, "Unmounting device...");
            if (root.filesystem("/dev/block/nanda").removeMount())
                //Use rmdir to prevent from important files removal!
                root.shell().run("rmdir /mnt/awse");
        }
        /*else if (root.filesystem("/dev/block/by-name/bootloader").isMounted()) {
            Log.v(TAG, "Unmounting device...");
            if (root.filesystem("/dev/block/nanda").removeMount())
                //Use rmdir to prevent from important files removal!
                root.shell().run("rmdir /mnt/awse");
        }*/
        else {
            Log.v(TAG, "Skipping unmount routine, cause loader isn't mounted");
        }
    }


    static void removeTempFiles()
    {
        java.io.File f = new java.io.File(Environment.getExternalStorageDirectory().getPath() + "/awse/script.bin");
        if(f.exists())
        {
            Log.i(TAG,"Removed temporatory script on " + Environment.getExternalStorageDirectory().getPath() + "/awse/");
            f.delete();
        }
    }

}
