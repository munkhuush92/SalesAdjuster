package com.example.munkhuush.salesadjuster;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.munkhuush.salesadjuster.Item.ItemContent;
import com.example.munkhuush.salesadjuster.SalesAdjusterDB.SalesAdjusterDB;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener, View.OnClickListener {
    private static String TOP_TEXT_FIRST = " TAP TO SELECT ";
    private static String TOP_TEXT_SECOND = "DISCOUNT";
    private static String MAIN_MODE = "main";
    private static String EDIT_MODE = "edit";
    private final int PLUS_SIGN_INDEX = 1;
    private final int MINUS_SIGN_INDEX = 0;
    private final StyleSpan NORMAL_FONT_STYLE = new StyleSpan(Typeface.NORMAL);
    private final StyleSpan BOLD_FONT_STYLE = new StyleSpan(Typeface.BOLD);
    private static boolean isFirstTime = true;
    private TextView mTextViewTop;
    //CURRENT SELECTED ID OF ITEM
    private int mCurrentSelectedID = -1;
    private String mCurrentSelectedType = "NULL";

    private String mMode;
    private static String cents[];
    private static String wholes[];

    //DB FILES
    private SalesAdjusterDB mFavoriteDB;
    private List<ItemContent.SaleItem> mFavoriteList;

    //Observerable
    private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

    //RIGHT SIDE ITEMS
    private LinearLayout rightSideBarLayout;
    private Button rightApplyBtn;
    private Button rightCustomBtn;
    private EditText rightTopEditText;
    private NumberPicker mWholeNumberPicker;
    private NumberPicker mFracNumberPicker;
    private LinearLayout rightSidePercentLayout;
    private LinearLayout rightSideAmountLayout;
    private EditText rightAmountEditText;
    //DIALOG ITEMS
    private Spinner dialogModeSpinner;
    private LinearLayout dialogPercentLayout;
    private LinearLayout dialogAmountLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // mode by default main
        mMode = MAIN_MODE;
        fillDB();
        if (findViewById(R.id.fragment_container)!= null) {
            //SENDING DATA TO FRAGMENT DB FILES
            Bundle bundle = new Bundle();
            bundle.putString("file", "data money");

            ItemFragment itemFrag = new ItemFragment();
            mPcs.addPropertyChangeListener(itemFrag);
            //if I set arguments, it will mess up grid layout into linear
            //itemFrag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,itemFrag )
                    .commit();
        }
        Button changeModeBtn = (Button) findViewById(R.id.changeModeBtn);
        rightApplyBtn =  (Button) findViewById(R.id.applyBtn);
        //set up apply btn for DB UPDATE
        rightApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mFavoriteDB.
                //update DB by ID
                if(mCurrentSelectedID!=-1){
                    if(mCurrentSelectedType.equals("amount")){
                        mFavoriteDB.updateFavoriteByID(mCurrentSelectedID, rightAmountEditText.getText().toString(), null, rightTopEditText.getText().toString(),"amount");
                    }else{
                        mFavoriteDB.updateFavoriteByID(mCurrentSelectedID, null, ""+wholes[mWholeNumberPicker.getValue()]
                                +"."+cents[ mFracNumberPicker.getValue()],rightTopEditText.getText().toString() ,"percent");
                    }

                }
                //refresh items grid
                mPcs.firePropertyChange("update grid",null, mFavoriteDB.getCourses());

            }
        });

        rightCustomBtn =  (Button) findViewById(R.id.customBtn);
        rightCustomBtn.setOnClickListener(this);
        rightTopEditText = (EditText) findViewById(R.id.rightEditText);
        rightSideBarLayout = (LinearLayout)this.findViewById(R.id.layoutRightSide);
        rightSidePercentLayout =(LinearLayout)this.findViewById(R.id.linearLayoutPercentMode2);
        rightSideAmountLayout = (LinearLayout)this.findViewById(R.id.linearLayoutAmountMode2);
        rightSideAmountLayout.setVisibility(View.GONE);
        rightAmountEditText = (EditText) this.findViewById(R.id.amount_editText);

        changeModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CURRENT MODE", mMode);
                if(mMode==MAIN_MODE){
                    mMode = EDIT_MODE;
                    //hide right container with number pickers
                    // hide apply btn

                    if(rightSideBarLayout!=null){
                        rightSideBarLayout.setVisibility(View.GONE);
                        rightApplyBtn.setVisibility(View.GONE);
                    }


                }else{
                    // main mode - cool
                    mMode = MAIN_MODE;
                    if(rightSideBarLayout!=null){
                        rightSideBarLayout.setVisibility(View.VISIBLE);
                        rightApplyBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        fillConstants();
        setUpNumberPickers();

        //  setting font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/museosans.ttf");
        mTextViewTop.setTypeface(typeface);
        //setting first part normal second part BOLD
        SpannableString str = new SpannableString(TOP_TEXT_FIRST + TOP_TEXT_SECOND);
        str.setSpan(NORMAL_FONT_STYLE, 0, TOP_TEXT_FIRST.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        str.setSpan(BOLD_FONT_STYLE, TOP_TEXT_FIRST.length(), (TOP_TEXT_FIRST+TOP_TEXT_SECOND).length(), Spannable.SPAN_MARK_POINT);
        mTextViewTop.setText(str);

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.mPcs.addPropertyChangeListener(listener);
    }

    private void fillDB() {

        mFavoriteDB = new SalesAdjusterDB(this);
        //IF WE WANT TO DELETE MANUALLY UNCOMMENT
        //mFavoriteDB.deleteFavorites();
        //    static {
        //insert 9 dummy data in DB
        if(isFirstTime){
            mFavoriteDB.insertFavorite(0, "107", "-20.75", "Summer Sale" , "percent");
            mFavoriteDB.insertFavorite(1, "101", "-10.75", "Happy Hour" , "percent");
            mFavoriteDB.insertFavorite(2, "102", "1.75", "Extra Charge", "amount" );
            mFavoriteDB.insertFavorite(3, "103", "-14.75", "Military Discount", "percent" );
            mFavoriteDB.insertFavorite(4, "104", "12.25", "Custom Tax" , "percent");
            mFavoriteDB.insertFavorite(5, "105", "-10.00", "10% discount" , "percent");
            mFavoriteDB.insertFavorite(6, "106", "-0.75", "Poop discount" , "percent");
            mFavoriteDB.insertFavorite(7, "107", "15.75", "Winter raise amount" , "amount");
            mFavoriteDB.insertFavorite(8, "107", "15.75", "Winter raise amount", "amount" );
            isFirstTime = false;
        }



        // Add some sample items.

        //read 9 data from DB pass to Fragment
        if(mFavoriteList==null) {
            mFavoriteList = mFavoriteDB.getCourses();
            Log.i("size of retur l", ""+mFavoriteList.size()) ;
            for (int i = 0; i < mFavoriteList.size(); i++) {
                ItemContent.addItem(mFavoriteList.get(i));
            }

        }
    }

    private ArrayList<ItemContent> createTestData(){
        return null;

    }

    private void setUpNumberPickers() {
        //SET UP NUMBER PICKERS
        mWholeNumberPicker=(NumberPicker) findViewById(R.id.whole_picker);
        mFracNumberPicker=(NumberPicker) findViewById(R.id.frac_picker);
        mTextViewTop = (TextView) findViewById(R.id.textViewTop);

        //adding number to number picker
        mFracNumberPicker.setMinValue(0);
        mFracNumberPicker.setMaxValue(15);
        mFracNumberPicker.setDisplayedValues(cents);

        mWholeNumberPicker.setMinValue(0);
        mWholeNumberPicker.setMaxValue(40);

        mWholeNumberPicker.setDisplayedValues(wholes);
        mWholeNumberPicker.setValue(20);

    }

    private void fillConstants() {

        cents = new String[20];
        for(int i = 0;i < 100; i+=5) {
            if( i < 10 )
                cents[i/5] = "0"+i;
            else
                cents[i/5] = ""+i;
        }

        wholes = new String[41];
        for(int i = 0;i <= 40; i++) {
            wholes[i] = ""+(i-20);
        }
    }

    @Override
    public void onListFragmentInteraction(ItemContent.SaleItem item) {
        Log.i("MODE" , mMode);
        //depends on mode onclick functions differ
        if(mMode!=MAIN_MODE){

        }else if(mMode!=EDIT_MODE){
            mCurrentSelectedID = item.favoriteID;
            mCurrentSelectedType = item.favoriteType;
            if(item.favoriteType.equals("amount")){
                rightSidePercentLayout.setVisibility(View.GONE);
                rightSideAmountLayout.setVisibility(View.VISIBLE);
                double amount = Double.parseDouble(item.favoriteAmount);
                rightAmountEditText.setText(""+amount);
                rightTopEditText.setText(item.favoriteName);
            }else{ //percent mode
                rightSidePercentLayout.setVisibility(View.VISIBLE);
                rightSideAmountLayout.setVisibility(View.GONE);
                //percent code
                double percent = Double.parseDouble(item.favoritePercent);
                int whole;
                int frac;
                String[] arr=String.valueOf(percent).split("\\.");
                whole=Integer.parseInt(arr[0]); // 1
                frac=Integer.parseInt(arr[1]); // 9
                mWholeNumberPicker.setValue(findIndex(wholes, whole));
                mFracNumberPicker.setValue(findIndex(cents, frac));
                //end of percent code

                rightTopEditText.setText(item.favoriteName);
            }




        }else{
            //ADD OTHER MODE

        }

    }

    private int findIndex(String[] list, int number){
        int retVal = -1;
        for(int i =0; i<list.length; i++){
            if((""+number).equals(list[i])){
                retVal = i;
            }

        }
        return retVal;

    }

    @Override
    public void onClick(View view) {

            numberPickerDialog();

    }
    private void numberPickerDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View theView = inflater.inflate(R.layout.fragment_blank, null);

        final NumberPicker unit_euro = (NumberPicker) theView.findViewById(R.id.euro_picker);
        final NumberPicker cent = (NumberPicker) theView.findViewById(R.id.cent_picker);
        final Spinner customSpinner = (Spinner) theView.findViewById(R.id.changeSpinner);
        dialogPercentLayout = (LinearLayout)theView.findViewById(R.id.linearLayoutPercentMode);
        dialogAmountLayout = (LinearLayout)theView.findViewById(R.id.linearLayoutAmountMode);
        //#SET UP SPINNER
        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Percent");
        spinnerArray.add("Amount");
      //  simple_spinner_item
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customSpinner.setAdapter(adapter);
        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    dialogAmountLayout.setVisibility(View.GONE);
                    dialogPercentLayout.setVisibility(View.VISIBLE);
                }else{

                    dialogAmountLayout.setVisibility(View.VISIBLE);
                    dialogPercentLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //END OF SPINNER


        builder.setView(theView)
                .setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //HERE After confirm let user choose which categories to add.
                        Log.d("DBG","Price is: "+unit_euro.getValue() + "."+cent.getValue());
                        Log.d("DBG", ""+ cents[cent.getValue()]);
                       // myCustomPrice = unit_euro.getValue() + "."+cents[cent.getValue()];
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        unit_euro.setMinValue(0);
        unit_euro.setMaxValue(40);
        unit_euro.setDisplayedValues(wholes);
        unit_euro.setValue(20);
        cents = new String[20];
        for(int i = 0;i < 100; i+=5) {
            if( i < 10 )
                cents[i/5] = "0"+i;
            else
                cents[i/5] = ""+i;
        }
        cent.setDisplayedValues(cents);

        cent.setMinValue(0);
        cent.setMaxValue(19);
        cent.setValue(0);

        builder.show();

    }
}
