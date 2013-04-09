package neo.droid.web.server;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ToggleButton toggleButton;
	private TextView textView;

	private boolean isWorking;
	private Intent intent;

	private int port;
	private String ipAddr;
	private String docRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		textView = (TextView) findViewById(R.id.textView);

		isWorking = false;

		textView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (false != isWorking) {
					return;
				}

				AlertDialog.Builder aBuilder = new AlertDialog.Builder(
						MainActivity.this);
				aBuilder.setTitle(R.string.configuration);
				View view = LayoutInflater.from(MainActivity.this)
						.inflate(R.layout.dailog_webserver_config, null);

				final EditText portEditText = (EditText) view
						.findViewById(R.id.edit_port);
				final EditText rootEditText = (EditText) view
						.findViewById(R.id.edit_doc_root);

				portEditText.setText("" + port);
				rootEditText.setText(docRoot);

				aBuilder.setView(view);
				aBuilder.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								port = Integer.parseInt(portEditText.getText()
										.toString());
								docRoot = rootEditText.getText().toString();
							}
						});

				aBuilder.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// [Neo] Empty
							}
						});

				aBuilder.create().show();

			}
		});

		Map<String, String> map = ResUtils.getLocalIpAddr();
		if (map.containsKey("eth0")) {
			ipAddr = map.get("eth0");
		} else if (map.containsKey("wlan0")) {
			ipAddr = map.get("wlan0");
		} else {
			ipAddr = "x";
		}

		port = WebService.DEFAULT_PORT;

		if (1 > ResUtils.getSDAvailableBytes()) {
			docRoot = getCacheDir().getPath();
		} else {
			docRoot = ResUtils.getSDDir();
		}

		intent = new Intent(MainActivity.this, WebService.class);
		
		toggleButton
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (false != isChecked) {
							intent.putExtra("port", port);
							intent.putExtra("root", docRoot);
							startService(intent);
							textView.setText("http://" + ipAddr + ":" + port
									+ "/");
						} else {
							stopService(intent);
							textView.setText(getString(R.string.app_name));
						}

						isWorking = isChecked;
					}
				});

	}

	@Override
	protected void onDestroy() {
		stopService(intent);
		super.onDestroy();
	}

}
