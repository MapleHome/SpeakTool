/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.pengenerations.lib.ble.streaming;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class PGGattAttributes {
	private static HashMap<String, String> attributes = new HashMap();
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	public static String PG_STREAMING_SERVICE_UUID = "00001B15-0000-1000-8000-00805f9b34fb";
	public static String PG_STREAMING_SERVICE_READ_UUID = "00008001-0000-1000-8000-00805f9b34fb";
	public static String PG_STREAMING_SERVICE_WRITE_UUID = "00008000-0000-1000-8000-00805f9b34fb";

	static {
		// 一般服务
		attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
		// PG Streaming Service
		attributes.put("00001b15-0000-1000-8000-00805f9b34fb", "PG Streaming Service");
		// General Characteristics.
		attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
		// PG Streaming Characteristics Read
		attributes.put("00008001-0000-1000-8000-00805f9b34fb", "Streaming Read String");
		// PG Streaming Characteristics Write
		attributes.put("00008000-0000-1000-8000-00805f9b34fb", "Streaming Write String");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		// return name == null ? defaultName : name;
		return name;
	}

	public static UUID String2UUID(String uuid) {
		// 创建 UUID
		UUID uid = UUID.fromString(uuid);
		return uid;
	}
}
