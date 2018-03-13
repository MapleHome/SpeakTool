package com.speaktool.tasks;

public interface IRunnable<Progress, Result> {
	
	void onPreExecute();

	Result doBackground();

	void onPostExecute(Result result);

	void onProgressUpdate(Progress... progress);

	void publishProgress(Progress... progress);

}
