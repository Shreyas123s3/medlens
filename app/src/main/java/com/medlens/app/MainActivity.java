package com.medlens.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medlens.app.assistant.OfflineAssistantService;
import com.medlens.app.assistant.QueryClassifier;
import com.medlens.app.data.MedLensDatabase;
import com.medlens.app.data.Medicine;
import com.medlens.app.data.SavedMedicine;
import com.medlens.app.safety.EmergencyDetector;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int NAVY = Color.rgb(20,59,69), TEAL = Color.rgb(24,122,120),
            PAPER = Color.rgb(250,249,245), MINT = Color.rgb(233,244,239), INK = Color.rgb(25,54,58),
            MUTED = Color.rgb(100,119,122), RED = Color.rgb(180,35,24), PALE_RED = Color.rgb(254,228,226);
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private MedLensDatabase database;
    private LinearLayout root, body, nav;
    private String currentTab = "Home", pendingSymptom = "";
    private int followup = 0;
    private SharedPreferences prefs;
    private OfflineAssistantService assistant;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = MedLensDatabase.get(this);
        prefs = getSharedPreferences("medlens", MODE_PRIVATE);
        assistant = new OfflineAssistantService();
        buildShell();
        showHome();
        if (!prefs.getBoolean("disclaimer_seen", false)) showDisclaimer();
    }
    @Override protected void onDestroy() { super.onDestroy(); io.shutdown(); }

    private void buildShell() {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(PAPER);
        setContentView(root);
        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(dp(22),dp(18),dp(22),dp(12));
        ImageView mark = new ImageView(this); mark.setImageResource(R.drawable.ic_medlens); mark.setContentDescription("MedLens logo"); header.addView(mark, new LinearLayout.LayoutParams(dp(38),dp(42)));
        TextView brand = text("MedLens", 23, NAVY, true); header.addView(brand);
        View spacer = new View(this); header.addView(spacer, new LinearLayout.LayoutParams(0,1,1));
        TextView status = text("●  OFFLINE", 11, TEAL, true); header.addView(status);
        root.addView(header);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        body = new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); body.setPadding(dp(20),dp(4),dp(20),dp(24));
        scroll.addView(body); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        nav = new LinearLayout(this); nav.setPadding(dp(8),dp(8),dp(8),dp(8)); nav.setBackgroundColor(Color.WHITE);
        String[] tabs = {"Home", "Explore", "Assistant", "Library"};
        for (String tab : tabs) { Button b = new Button(this); b.setText(tab); b.setTextSize(11); b.setAllCaps(false); b.setTextColor(TEAL); b.setMinWidth(0); b.setPadding(dp(4),0,dp(4),0); nav.addView(b,new LinearLayout.LayoutParams(0,dp(48),1)); b.setOnClickListener(v -> navigate(tab)); }
        root.addView(nav);
    }

    private void navigate(String tab) { currentTab = tab; switch (tab) { case "Explore": showExplore(""); break; case "Assistant": showAssistant(); break; case "Library": showLibrary(); break; default: showHome(); } }
    private void showHome() {
        body.removeAllViews();
        TextView eyebrow = text("YOUR EVERYDAY HEALTH COMPANION", 12, TEAL, true); add(eyebrow,0,0,0,12);
        TextView title = text("Know more.\nFeel more informed.", 30, NAVY, true); add(title,0,0,0,12);
        TextView subtitle = text("Explore medicine information and organize your symptoms, privately and offline.",16,MUTED,false); add(subtitle,0,0,0,20);
        EditText search = input("Search medicines"); add(search,0,0,0,12); search.setOnEditorActionListener((v,a,event) -> { showExplore(search.getText().toString()); return true; });
        Button find = button("Explore medicines"); add(find,0,0,0,22); find.setOnClickListener(v -> showExplore(search.getText().toString()));
        card("A little more clarity", "Browse a small offline catalog. Every medicine detail not yet reviewed by a pharmacist is marked unavailable.");
        Button chat = button("Open symptom assistant"); add(chat,0,18,0,12); chat.setOnClickListener(v -> navigate("Assistant"));
        TextView disclaimer = text("MedLens provides general information, not diagnosis or treatment. In an emergency in India, call 112.",13,MUTED,false); add(disclaimer,0,14,0,12);
    }
    private void showExplore(String initial) {
        body.removeAllViews(); add(text("Explore",29,NAVY,true),0,0,0,6);
        add(text("Search the offline medicine catalog by generic name or category.",14,MUTED,false),0,0,0,16);
        EditText search = input("Medicine or category"); search.setText(initial); add(search,0,0,0,10);
        LinearLayout results = new LinearLayout(this); results.setOrientation(LinearLayout.VERTICAL); body.addView(results);
        Runnable load = () -> io.execute(() -> {
            List<Medicine> list = database.medicineDao().search(search.getText().toString().trim());
            runOnUiThread(() -> { if (isFinishing()) return; results.removeAllViews();
                if (list.isEmpty()) { results.addView(text("No medicines match this search yet.",15,MUTED,false)); return; }
                for (Medicine m : list) { Button item = button(m.genericName + "  ·  " + m.category); item.setGravity(Gravity.START|Gravity.CENTER_VERTICAL); results.addView(item); item.setOnClickListener(v -> showMedicine(m)); }
            });
        });
        load.run(); search.addTextChangedListener(new TextWatcher() { public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int before,int count){ load.run(); } public void afterTextChanged(Editable e){} });
    }
    private void showMedicine(Medicine m) {
        currentTab="Explore"; body.removeAllViews();
        Button back=button("←  Back to Explore"); add(back,0,0,0,12); back.setOnClickListener(v->showExplore(""));
        add(text(m.genericName,29,NAVY,true),0,0,0,4); add(text(m.category,14,TEAL,true),0,0,0,16);
        Button save=button("Save / remove bookmark"); add(save,0,0,0,12); save.setOnClickListener(v->io.execute(()->{
            if(database.savedMedicineDao().isSaved(m.genericName)) database.savedMedicineDao().remove(m.genericName); else database.savedMedicineDao().save(new SavedMedicine(m.genericName));
            runOnUiThread(()->android.widget.Toast.makeText(this,"Saved list updated",android.widget.Toast.LENGTH_SHORT).show());
        }));
        field("Brands",m.brands); field("Uses",m.uses); field("Mechanism",m.mechanism); field("Side effects",m.sideEffects);
        field("Precautions",m.precautions); field("Contraindications",m.contraindications); field("Interactions",m.interactions);
        field("Storage",m.storage); field("When to see a doctor",m.whenToSeeDoctor); field("Source",m.source);
        card("Please note", "This starter catalog is incomplete and needs review by a pharmacist or doctor before real-world use. It does not contain dosage advice.");
    }
    private void field(String name,String value){ TextView t=text(name+"\n"+value,15,INK,false); t.setPadding(dp(14),dp(12),dp(14),dp(12)); t.setBackgroundColor(Color.WHITE); add(t,0,4,0,4); }
    private void showLibrary(){
        body.removeAllViews(); add(text("Your library",29,NAVY,true),0,0,0,8); card("Saved medicines", "Bookmarks are stored on this device.");
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); body.addView(list);
        io.execute(()->{ List<SavedMedicine> saved=database.savedMedicineDao().all(); runOnUiThread(()->{
            if(saved.isEmpty()) list.addView(text("No saved medicines yet. Explore the catalog and save one to find it here.",15,MUTED,false));
            for(SavedMedicine item:saved){ Button b=button(item.genericName); list.addView(b); b.setOnClickListener(v->io.execute(()->{Medicine m=database.medicineDao().get(item.genericName); if(m!=null)runOnUiThread(()->showMedicine(m));})); }
        });});
        Button settings=button("Settings and safety information"); add(settings,0,18,0,8); settings.setOnClickListener(v->showDisclaimer());
    }
    private void showAssistant(){
        body.removeAllViews(); add(text("Symptom assistant",28,NAVY,true),0,0,0,4);
        add(text("Offline mode · Your messages stay on this device",13,TEAL,true),0,0,0,12);
        card("Before we begin", "This assistant cannot diagnose or recommend medicines. For emergencies, call 112 in India.");
        EditText entry=input("Describe what you’re experiencing"); add(entry,0,10,0,8);
        Button send=button("Send"); add(send,0,0,0,10); send.setOnClickListener(v->{String q=entry.getText().toString().trim(); if(q.isEmpty())return; entry.setText(""); handleSymptom(q);});
        if (!prefs.getString("chat","").isEmpty()) {
            TextView history=text(prefs.getString("chat",""),15,INK,false); history.setPadding(dp(12),dp(12),dp(12),dp(12)); history.setBackgroundColor(Color.WHITE); add(history,0,4,0,10);
        }
        Button reset=button("Start a new chat / clear history"); add(reset,0,8,0,12); reset.setOnClickListener(v->{prefs.edit().remove("chat").apply();pendingSymptom="";followup=0;showAssistant();});
    }
    private void handleSymptom(String q){
        if(EmergencyDetector.isEmergency(q)){ appendChat("You: "+q+"\nMedLens: This may be an emergency. Call 112 in India now or go to the nearest emergency department. Do not wait for an online response."); showEmergency(); return; }
        if(pendingSymptom.isEmpty()){
            if(QueryClassifier.isMedicineQuery(q)){
                String normalized=q.toLowerCase();
                String[] names={"paracetamol","ibuprofen"};
                String found=null;
                for(String name:names) if(normalized.contains(name)){found=name;break;}
                if(found==null){appendChat("You: "+q+"\nMedLens: I can search the local catalog. What generic name should I look up?");showAssistant();return;}
                final String generic=found;
                io.execute(()->{Medicine m=database.medicineDao().get(generic.substring(0,1).toUpperCase()+generic.substring(1));runOnUiThread(()->{
                    String response=m==null?"That medicine is not in the local catalog.":"The catalog entry for "+m.genericName+" has no reviewed clinical details yet. The available fields are marked Information unavailable. It needs review by a pharmacist or doctor before real-world use.";
                    appendChat("You: "+q+"\nMedLens: "+response);showAssistant();
                });});return;
            }
            pendingSymptom=q;followup=0;
            appendChat("You: "+q+"\nMedLens: "+assistant.answer(q)); showAssistant();return;
        }
        pendingSymptom += " | " + q; followup++;
        if(followup < 4){ String[] questions={"How severe are the symptoms: mild, moderate, or severe?","What other symptoms do you have?","Are you an adult, older adult, or a child? Is pregnancy or breastfeeding relevant?"};
            appendChat("You: "+q+"\nMedLens: "+questions[Math.min(followup,questions.length-1)]); showAssistant();
        } else {
            appendChat("You: "+q+"\nMedLens summary\n• Symptoms you described: "+pendingSymptom+"\n• This is not a diagnosis. No medicine recommendation is made.\n• Contact a clinician if symptoms worsen, persist, or concern you.");
            pendingSymptom="";followup=0;showAssistant();
        }
    }
    private void appendChat(String entry){String old=prefs.getString("chat",""); String updated=(old.isEmpty()?"":old+"\n\n")+entry; prefs.edit().putString("chat",updated).apply();}
    private void showEmergency(){
        TextView warning=text("CALL 112 IN INDIA NOW\n\nYour message may describe a medical emergency. Go to the nearest emergency department. Do not wait for this app.",18,RED,true);
        warning.setPadding(dp(18),dp(18),dp(18),dp(18));warning.setBackgroundColor(PALE_RED);
        new MaterialAlertDialogBuilder(this).setTitle("Emergency help needed").setView(warning)
                .setCancelable(false).setPositiveButton("Understood",(d,w)->{}).show();
    }
    private void showDisclaimer(){
        new MaterialAlertDialogBuilder(this).setTitle("Important health information")
                .setMessage("MedLens is an informational tool, not a medical professional. It does not diagnose, prescribe, or provide dosing. Medicine information is incomplete and needs review by a pharmacist or doctor before real-world use. In India, call 112 for emergencies.")
                .setCancelable(false).setPositiveButton("I understand",(d,w)->prefs.edit().putBoolean("disclaimer_seen",true).apply()).show();
    }
    private void card(String title,String message){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(16),dp(16),dp(16));c.setBackgroundColor(MINT);c.addView(text(title,17,NAVY,true));TextView m=text(message,14,INK,false);m.setPadding(0,dp(6),0,0);c.addView(m);add(c,0,10,0,6);}
    private Button button(String label){Button b=new Button(this);b.setText(label);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setTextSize(14);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(TEAL));return b;}
    private EditText input(String hint){EditText e=new EditText(this);e.setSingleLine();e.setTextSize(15);e.setHint(hint);e.setPadding(dp(14),dp(8),dp(14),dp(8));e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(TEAL));return e;}
    private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private void add(View v,int left,int top,int right,int bottom){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(dp(left),dp(top),dp(right),dp(bottom));body.addView(v,p);}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
}
