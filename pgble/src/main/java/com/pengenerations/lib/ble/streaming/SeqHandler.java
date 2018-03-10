package com.pengenerations.lib.ble.streaming;


public class SeqHandler {
	final String TAG = "SeqHandler";

	boolean m_bfSeqStatus = true;
	byte m_CurSeq;

	public SeqHandler(byte seq) {
		m_CurSeq = seq;
		m_bfSeqStatus = true;
	}

	public boolean SetCurSeq(byte seq) {
		// Log.d(TAG,"Old Seq : " + m_StartSeq + ", Cur Seq : " + seq);
		if (m_CurSeq != seq) {
			// Log.e(TAG, "Cur Seq : " + m_CurSeq + ", Rcv Seq : " + seq);
			// m_CurSeq = (byte)(seq + (byte)1);
			m_bfSeqStatus = false;
			return false;
		}
		// Log.d(TAG, "Cur Seq : " + m_CurSeq + ", Rcv Seq : " + seq);
		m_CurSeq = (byte) (seq + (byte) 1);
		return true;
	}

	public boolean GetSeqStatus() {
		return m_bfSeqStatus;
	}

	public void initSeq(byte seq) {
		m_bfSeqStatus = true;
		m_CurSeq = seq;
	}
}
