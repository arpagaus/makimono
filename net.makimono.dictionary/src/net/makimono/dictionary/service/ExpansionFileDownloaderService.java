package net.makimono.dictionary.service;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class ExpansionFileDownloaderService extends DownloaderService {
	public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwlyDw1iG9oNkfhypwjupjoiuT/TGRWpgCe9wdE8f5M7fqj4RAmli5ZIto5nHgadGFnblZZIkBHxpNhzAivr1Kw+UyEvBeRkbzeJj762xre5BG+6iYm7deyEiVRRoElShnaaJSeWK3uY3zt5KKghcyJbkZeW7XCq1Z+fukViC+14hFSHT8tMHi/qMpjziog1zdS5p1prDmRUHL2HlCuWu0c402f8RWs9EYZ8PifUH1z7vlpDPNTS8Ozne3Z/WcACtv2Qk4eQANAHYv2BFKFrwOJYPzygbtBC/btrD6YNInNO4itYZanOPoITMm2XSuMs+jD3uXrL+9QMsh2LXxtkrZQIDAQAB";
	public static final byte[] SALT = new byte[] { 121, -30, -115, 52, -71, -15, -50, 14, -128, 9, 87, 80, 78, -82, -57, -115, -110, -110, -70, -92 };

	@Override
	public String getPublicKey() {
		return BASE64_PUBLIC_KEY;
	}

	@Override
	public byte[] getSALT() {
		return SALT;
	}

	@Override
	public String getAlarmReceiverClassName() {
		return ExpansionFileAlarmReceiver.class.getName();
	}
}
