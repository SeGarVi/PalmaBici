package com.poguico.palmabici;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreditsDialog extends Dialog {

	Button close_button, web_button, agpl_button, apache_button, code_button;
	Context context;
	Dialog self;
	
	public CreditsDialog(Context context) {
		super(context);
		this.context = context;
		self = this;
		// TODO Auto-generated constructor stub
		this.setContentView(R.layout.credits_layout);
		this.setTitle(R.string.menu_credits);
		
		web_button    = (Button)findViewById(R.id.button_visit_web);
		code_button   = (Button)findViewById(R.id.button_source_code);
		agpl_button   = (Button)findViewById(R.id.button_agpl_license);
		apache_button = (Button)findViewById(R.id.button_apache_license);
		close_button  = (Button)findViewById(R.id.button_close);
		
		web_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        open_url("https://github.com/SeGarVi/PalmaBici/wiki");
		        self.hide();
		    }
		});
		
		code_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        open_url("https://github.com/SeGarVi/PalmaBici");
		        self.hide();
		    }
		});
		
		agpl_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        open_url("https://www.gnu.org/licenses/agpl-3.0.en.html");
		        self.hide();
		    }
		});
		
		apache_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        open_url("https://www.apache.org/licenses/LICENSE-2.0");
		        self.hide();
		    }
		});
		
		close_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	self.hide();
		    }
		});
		
		Linkify.addLinks((TextView)findViewById(R.id.label_author), Linkify.EMAIL_ADDRESSES);
	}

	private void open_url(String url) {
		Intent open_url_activity = new Intent(Intent.ACTION_VIEW);
		open_url_activity.setData(Uri.parse(url));
		context.startActivity(open_url_activity);
	}
}