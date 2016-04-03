package course.examples.theanswer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TheAnswer extends Activity {

	// URL Address
	String url = "http://www.nyu.edu";
	ProgressDialog mProgressDialog;
	public static final String detText = " ";
	TextView summaryView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String mdetText = " ";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer_layout);

		if (savedInstanceState != null) {
			mdetText = savedInstanceState.getString(detText," ");
		}

		final EditText urlText = (EditText) findViewById(R.id.urlText);
		Button button = (Button) findViewById(R.id.button);
		summaryView = (TextView) findViewById(R.id.summaryView);

		summaryView.setText(mdetText);

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// Execute Title AsyncTask
				url = urlText.getText().toString();
				new Title().execute();
			}
		});
	}

	// Title AsyncTask needed as synchronous call to url is not allowed in android to avoid freezing of device
	private class Title extends AsyncTask<Void, Void, Void> {
		String sumNdet;
		String[] cmnWords = new String[] {"a","an","the","is","are","was","were", "has","had","been", "will","would", "can","could","shall","should",
				"I","you","he","she","me","guy","girl","gal","don't","It's","it","they","mine","your","our","his","her","him","their","these","those","do","did",
				"and","mention","New","really","several","high","low","faster","have","had","will","at","The","be","being","been","many","some","few",
				"by","for","during","During","just","over","with","there","then","in","it","even","if","other","now","could","would","should","so","such",
				"while","than","and","been","an","be","to","this","that","of","or","but","But","its","he","He","between","you","You","I","he","they","from",
				"said","on","to","one","two","three","four","five","six","seven","eight","nine","ten","year","years","month","months","day","days",
				"January","February","March","April","May","June","July","August","September","October","November","December","before","after",
				"see","all","last","first","second","third","very","few","number","before","after","what","when","where","how","always","never","such",
				"in","into","more","—","about","”","also","remaining","similar","happen","somebody","To","not","mine","your","go","going","gone","went",
				"get","put","because","still","such","great","who","whose","whom","up","down","so","as",
				"them","us","out","in"};
		String bodycontent[];
		String tmpStr = " ";
		String paraStr = " ";
		Map<String, Integer> map = new HashMap<String, Integer>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(TheAnswer.this);
			mProgressDialog.setTitle("Srishti Sanya Capone SES");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// Connect to the web site
				Document document = Jsoup.connect(url).get();
				// Get the html document title
				sumNdet = "Title : \n" + document.title() + "\n\n";

/***************Cut from this section *************/
				Elements paras = document.select("article[itemprop=\"articleBody\"]");
				bodycontent = paras.text().split("[ .,]+");
				//
// 				Isolate the body in paragraphs so that we can find the most important para
				paraStr = " ";
				int i = 0;
				for (i=0; i<paras.select("p").size();i++) {
					paraStr = paraStr + paras.select("p").get(i).text() + "\n";
				}
				sumNdet = sumNdet + "Summary:" + "\n" + paras.select("p").get(0).text() + "\n\n";
				Elements author = document.select("span[itemprop=\"name\"]");
				sumNdet = sumNdet + "Author: \n" + author.text() + "\n\n";
				Elements authorBio = document.getElementsByClass("post-body-bio");
				sumNdet = sumNdet + "Author Bio: \n" + authorBio.text() + "\n\n";
				Elements links = document.body().select("a[href^=http:]");
				tmpStr =" ";
				for (i=0; i<links.size();i++) {
					if (i >10){break;}
					tmpStr = tmpStr + links.get(i).attr("href").toString() + "\n";
				}
				sumNdet = sumNdet + "Links on the page: \n" + tmpStr + "\n\n";
				sumNdet = sumNdet + "Total Number of Paragraphs: " + paras.select("p").size() + "\n";

//				Brought in from top

//				For testing purposes, comment the two lines above and uncomment the two lines below
//				String paras = "robin, kumar. Srishti, sanya, mary. disco, kumar. ";
//				bodycontent = paras.split("[ .,]+");
// 				Create a word and frequency list only if the word is not a common word listed above

				for (String keyword: bodycontent){
					Integer n = map.get(keyword);
					n = (n == null) ? 1: ++n;
					if (!Arrays.asList(cmnWords).contains(keyword)){map.put(keyword.toLowerCase(),n);}
				}
//				Print the frequenctly used word criteria frequency>3, common words have been removed
				sumNdet = sumNdet + "Most important keywords : \n";
				tmpStr = " ";
				for (Object variableName: map.keySet()){

					if (map.get(variableName) > 5){
						tmpStr = tmpStr + variableName +" : " + map.get(variableName) + "\n";
					}
				}
				sumNdet = sumNdet + tmpStr;

//				End of keyword identification
				sumNdet = sumNdet + "Original Article:" + "\n" + paraStr;
				paraStr = " ";
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// Set title into TextView
//			TextView summaryView = (TextView) findViewById(R.id.summaryView);
			summaryView.setMovementMethod(new ScrollingMovementMethod());
			summaryView.setText(sumNdet);
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save the values you need from your textview into "outState"-object
		super.onSaveInstanceState(outState);
		outState.putString(detText, summaryView.getText().toString());
	}
}
