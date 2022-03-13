package com.example.sheedah;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class NewInvoicePage extends AppCompatActivity implements  View.OnClickListener,  AdapterView.OnItemSelectedListener{
    Button addbtn;
    ArrayList<Customer> customersList;
    ArrayList <DailySales> dailySalesList;
    Spinner catSpinner , customerSpinner; //drop down for selecting a customer from existing customers details
    TextInputEditText amount_field, quantity_field, amount_paid;
    TextInputLayout amount_field_lay, amount_paid_lay, quantity_field_lay;
    FloatingActionButton newItemBtn;
    ScrollView newInvoiceScroll;
    ProgressBar invoiceGenPb;
    LinearLayout customerChoiceLay;
    Toolbar backBar;
    final int PERM_REQUEST_CODE=123456;
    //RelativeLayout newInvoiceTopBayLay;
    //stores boolean value for a customer that holds debt or not
    // True for a debtor and false for otherwise
    boolean is_debt;
    String debtStatement, customer_phoneNo, product_category, customer_tag, customer_name;
    Double debt;

    //database related classes for caching recent transactions
    DatabaseReference customerReference;
    RecentTransactionDb recentTransactionDb;
    SQLiteDatabase sqLiteDatabase;

    //customer spinner adapter declaration
    ArrayAdapter <String> customerArrayAdapter;

    //new declarations
    ArrayList <String>productTypeList;
    ArrayList <String>amountList;
    ArrayList <String>amountPaidList;
    ArrayList <String>quantityList;

    SharedPreferences sharedPreferences;








    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchVal= sharedPreferences.getBoolean("theme",false);
        if (switchVal) {
            setTheme(R.style.sheedah_dark);
        }

        else {
            setTheme(R.style.sheedah);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_invoice_page);
        requestForStoragePermission();
        addbtn=findViewById(R.id.addBtn);
        addbtn.setOnClickListener(this);
        backBar=findViewById(R.id.backBar);
        backBar.setNavigationOnClickListener(onClickBackBar);
        catSpinner=findViewById(R.id.product_category);
        customerSpinner=findViewById(R.id.customer);
        amount_field=findViewById(R.id.amount_field);
        amount_field.addTextChangedListener(amountTextWatcher);
        quantity_field=findViewById(R.id.quantity_field);
        quantity_field.addTextChangedListener(quantityTextWatcher);
        amount_paid=findViewById(R.id.amount_paid);
        amount_paid.addTextChangedListener(amountPaidTextWatcher);
        amount_field_lay=findViewById(R.id.amount_field_lay);
        amount_paid_lay=findViewById(R.id.amount_paid_lay);
        quantity_field_lay=findViewById(R.id.quantity_field_lay);
        catSpinner.setOnItemSelectedListener(this);
        customerSpinner .setOnItemSelectedListener(this);
        newItemBtn= findViewById(R.id.newItemBtn);
        newItemBtn.setOnClickListener(this);
        newInvoiceScroll= findViewById(R.id.newInvoiceScroll);
        invoiceGenPb=findViewById(R.id.invoiceGenerationPb);
        customerChoiceLay=findViewById(R.id.customerChoiceLay);
        //newInvoiceTopBayLay= findViewById(R.id.newInvoiceTopBayLay);
        customersList=HomePageActivity.customersList;
        dailySalesList=HomePageActivity.dailySalesList;
        customerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,getCustomerNamesAndPhoneNo());
        customerSpinner.setAdapter(customerArrayAdapter);
        //creating array adapter from array resource file in res/values/array.xml
        //customersList=new ArrayList<Customer>();
        ArrayAdapter <CharSequence> catSpinnerAdapter= ArrayAdapter.createFromResource(this, R.array.product_categories, android.R.layout.simple_spinner_dropdown_item);
        catSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(catSpinnerAdapter);

        //initiating new declarations
        productTypeList= new ArrayList<>();
        amountPaidList= new ArrayList<>();
        amountList= new ArrayList<>();
        quantityList = new ArrayList<>();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.addBtn:
                //the function that would get called when the add button is clicked
                onAddButtonClicked();
                break;
            case R.id.newItemBtn:
                onNewItemBtnClicked();

        }
    }


    public void onAddButtonClicked() {
        if (!(customerArrayAdapter.isEmpty())) {
            addToPurchaseItem(amount_field.getText().toString(), quantity_field.getText().toString(), amount_paid.getText().toString(), product_category);
            newInvoiceScroll.setVisibility(View.GONE);
            invoiceGenPb.setVisibility(View.VISIBLE);
            Toast.makeText(NewInvoicePage.this, "Wait... invoice generation started", Toast.LENGTH_SHORT).show();
            addbtn.setVisibility(View.GONE);
            submitInvoice();
        }

        else {
            Toast.makeText(this,"Empty customer list, screen will reload!",Toast.LENGTH_SHORT).show();
            onRestart();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId())
        {
            case R.id.customer:
               customer_phoneNo= onCustomerSelected(parent.getItemAtPosition(position).toString(), position);
                break;
            case R.id.product_category:
                product_category=onProductCategorySelected(parent.getItemAtPosition(position).toString());
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            finishAfterTransition();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do something
    }


    public String  onProductCategorySelected (String category) {
           //do something
        //Toast.makeText(this, category, Toast.LENGTH_LONG).show();
        return category;
    }


    //function handler for selecting a customer
    public String   onCustomerSelected (String customer,int position) {
        customer_tag=customer;
        is_debt=customersList.get(position).debtStatus;
        debtStatement=customersList.get(position).debtStatement;
        String regexPattern="_";
        String customerSplit[]=customer.split(regexPattern);
        customer_name=customerSplit[0];
        //Toast.makeText(this, customersList.get(position).toString(), Toast.LENGTH_LONG).show();
        return customerSplit[1];
    }

    //helps to format the display of customer list in customer spinner
    public String[] getCustomerNamesAndPhoneNo() {
            ArrayList<String> customerNamesAndPhoneNo=new ArrayList<String>();
            for (Customer customer: customersList) {
                customerNamesAndPhoneNo.add(customer.customerName+"_"+customer.phoneNumber);
            }
            return  customerNamesAndPhoneNo.toArray(new String[customerNamesAndPhoneNo.size()]);
    }

    public void validateForm () {
        String amount= amount_field.getText().toString();
        String quantity =quantity_field.getText().toString();
        String amountPaid = amount_paid.getText().toString();

        if (!(amount.isEmpty() && quantity.isEmpty() && amountPaid.isEmpty())) {
            addbtn.setEnabled(true);
            newItemBtn.setEnabled(true);
        }
        if ((amount.isEmpty() && quantity.isEmpty() && amountPaid.isEmpty())) {
            addbtn.setEnabled(false);
            newItemBtn.setEnabled(false);
        }
    }

    TextWatcher amountTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            if (count>0) {
                amount_field_lay.setErrorEnabled(false);
                validateForm();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty() ) {
                amount_field_lay.setError("Amount is required");
                amount_field_lay.setErrorEnabled(true);
                addbtn.setEnabled(false);
                newItemBtn.setEnabled(false);
            }

            else    {
                addbtn.setEnabled(true);
                newItemBtn.setEnabled(true);
            }
        }
    };

    TextWatcher quantityTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            if (count>0) {
               quantity_field_lay.setErrorEnabled(false);
               validateForm();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                quantity_field_lay.setError("Quantity is required");
                quantity_field_lay.setErrorEnabled(true);
                addbtn.setEnabled(false);
                newItemBtn.setEnabled(false);
            }

            else {
                addbtn.setEnabled(true);
                newItemBtn.setEnabled(true);
            }
        }
    };

    TextWatcher amountPaidTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            if (count>0) {
                amount_paid_lay.setErrorEnabled(false);
                validateForm();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                amount_paid_lay.setError("Amount paid is required");
                amount_paid_lay.setErrorEnabled(true);
                addbtn.setEnabled(false);
                newItemBtn.setEnabled(false);
            }

            else  {
                addbtn.setEnabled(true);
                newItemBtn.setEnabled(true);
            }
        }
    };

    //function that performs the calculation and push final update to the server

    public void submitInvoice () {
            //Double amountDouble=0.00;
            Double amountPaidDouble=0.00;
            Double totalAmount= getTotalAmount();

            //initaite new invoice data
          //  Map <String, Map> newTransactionData =new HashMap<String, Map>();
            Map <String, Object> transactionDetails= new HashMap();

                if (productTypeList.size()==1) {
                    Map <String, Object>itemMap = new HashMap<>();
                    itemMap.put("amount",amountList.get(0));
                    itemMap.put("quantity",quantityList.get(0));
                    itemMap.put("product",productTypeList.get(0));
                    Map <String, Map> items = new HashMap<>();
                    items.put("0",itemMap);
                    transactionDetails.put("items",items);
                    amountPaidDouble=Double.parseDouble(amountPaidList.get(0));
                }

                 else if (productTypeList.size()>1) {
                     Map <String,Map> items= new HashMap<>();
                     for (int i=0; i<productTypeList.size();i++) {
                       Map <String, Object>itemMap = new HashMap<>();
                       itemMap.put("amount",amountList.get(i));
                      itemMap.put("quantity",quantityList.get(i));
                      itemMap.put("product",productTypeList.get(i));
                       items.put(String.valueOf(i),itemMap);
                       amountPaidDouble=Double.parseDouble(amountPaidList.get(i)) + amountPaidDouble;
                      }
                     transactionDetails.put("items",items);
                }


            debt=totalAmount-amountPaidDouble;
            Double debtStatementDouble=Double.parseDouble(debtStatement);
            //Toast. makeText(this,debtStatement,Toast.LENGTH_SHORT).show();
            debt=debt+(debtStatementDouble);

            //change the debt status based on the debt value
            if (debt>0 && debt>0.00)
                is_debt=true;
            else
                is_debt=false;

            Map <String, Object> updateCustomer=new HashMap<String, Object>();

            //get date and time to use as key for the transactionDetails in newTransactionData map
           // LocalDateTime currentDateAndTime= LocalDateTime.now();
            //DateTimeFormatter  currentDateAndTimeFormatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            //currentDateAndTime.toString();
            SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar c= Calendar.getInstance();
            String date=sdf.format(c.getTime());
            //newTransactionData.put(date,transactionDetails);

            //getting date and time differently for taking cache of transaction in recent transaction database
            sdf.applyPattern("yyyy-MM-dd");
            String dateOnly=sdf.format(c.getTime());
            sdf.applyPattern("HH:mm:ss");
            String timeOnly=sdf.format(c.getTime());


            //put updates to updateCustomer map
            updateCustomer.put("debtStatement",debt.toString());
            updateCustomer.put("debtStatus", is_debt);

            //adding the time and date  to the transaction details map
            transactionDetails.put("date",dateOnly);
            transactionDetails.put("time",timeOnly);

            //get database reference then update customer's transaction history and data
            customerReference= FirebaseDatabase.getInstance().getReference().child("Customers").child(customer_phoneNo);
            customerReference.updateChildren(updateCustomer).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    customerReference.child("transactionHistory").child(date).setValue(transactionDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateDailySales(getTotalSales(dateOnly),totalAmount,dateOnly);
                            ContentValues transactionValues=new ContentValues();
                            transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_1,customer_tag);
                            transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_2,totalAmount);
                            //if is_debt is false, transaction status is on credit
                            // if it is true, transactionstatus is clear

                            if (is_debt) {
                                transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_3,"On credit");
                            }

                            else  {
                                transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_3,"Clear");
                            }
                            transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_4,timeOnly);
                            transactionValues.put(SheedahLocalDatabaseContract.RecentTransactions.COLUMN_NAME_5,dateOnly);
                            sqLiteDatabase.insert(SheedahLocalDatabaseContract.RecentTransactions.TABLE_NAME, null, transactionValues);
                            Toast.makeText(NewInvoicePage.this,"Customer transaction recorded successfully",Toast.LENGTH_SHORT).show();
                            try {

                                generateInvoice(customer_name,totalAmount.toString(),date,debt.toString());
                            } catch (Exception e) {
                                Toast.makeText(NewInvoicePage.this, "Can't generate invoice!",Toast.LENGTH_SHORT).show();
                                finishAfterTransition();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            Toast.makeText(NewInvoicePage.this,"Customer transaction record failed!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure (Exception e) {
                    Toast.makeText(NewInvoicePage.this,"Customer transaction record failed!",Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override

    protected void onStart() {
        recentTransactionDb= new RecentTransactionDb(this);
        sqLiteDatabase=recentTransactionDb.getWritableDatabase();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        recentTransactionDb.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCustomer (Customer customer) {
        customersList.add(customer);
        Toast.makeText(this, customer.customerName, Toast.LENGTH_SHORT).show();

    }

    //function for getting the current daily sales in order to be added to the new sale
    public Double getTotalSales(String date) {
        if (dailySalesList.size()>0) {
            for (DailySales sales : dailySalesList) {
                if (sales.date.equals(date)) {
                    return sales.totalSales;
                }
            }
        }
        return 0.00;
    }

    //function for updating daily sales
    public void updateDailySales (double totalSales, double newSales, String dateOnly) {
        DatabaseReference dailySalesReference =FirebaseDatabase.getInstance().getReference().child("Daily-sales").child(dateOnly);
        DailySales dailySales;
        if (totalSales>0 && totalSales>0.00) {
            dailySales=new DailySales(dateOnly, (totalSales+newSales));
            dailySalesReference.setValue(dailySales);
        }

        else {
            dailySales =new DailySales(dateOnly,newSales);
            dailySalesReference.setValue(dailySales);
        }
    }

    void generateInvoice (String customer_name,  String totalAmount , String paymentDate, String debtStatement) throws Exception {

        //refactoring invoice data
        SimpleDateFormat originalDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = originalDateFormat.parse(paymentDate);
        DateFormat newDateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ssa", Locale.ENGLISH);
        newDateFormat.setTimeZone(TimeZone.getDefault());
        String newDate = newDateFormat.format(date);
        String invoiceNo=paymentDate.replace('-',' ').replace(':',' ').replaceAll(" ","")+ new Random().nextInt(50);
        customerReference.child("transactionHistory").child(paymentDate).child("invoiceNo").setValue(invoiceNo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused)
            {
                //creating pdf file path
                //String pdfPath= (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/sheedah").toString();
                File dir = new File(getExternalFilesDir(null),"/sheedah");
                if (!dir.exists()  ) {
                    dir.mkdir();
                }

                File file = new File(dir, customer_name+"_"+paymentDate+".pdf");
                if (!file.isDirectory() || !file.exists()) {
                    //Environment.getStorageDirectory().mkdir();
                }
                try {
                    OutputStream outputStream= new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    //a very unlikely error that can occur, which alert if there is an invalid file path defined which is very close not to  occur
                    Toast.makeText(NewInvoicePage.this,"Couldn't generate invoice due to technical issue encounted. Contact your developer", Toast.LENGTH_SHORT).show();
                    return;
                }

                //creating pdf file
                PdfWriter writer= null;
                try {
                    writer = new PdfWriter(file);
                } catch (FileNotFoundException e) {
                    //a very unlikely error that can occur, which alert if there is an invalid file path defined which is very close not to  occur
                    Toast.makeText(NewInvoicePage.this,"Couldn't generate invoice due to technical issue encounted. Contact your developer", Toast.LENGTH_SHORT).show();
                    return;
                }
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document invoiceDocument = new Document(pdfDocument);

                //creating paragraph
                //Getting logo to put on invoice
                Drawable invoiceLogo = getDrawable(R.drawable.sheedah_logo);
                Bitmap bitmap=((BitmapDrawable)invoiceLogo).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,byteArrayOutputStream);
                byte[] bitmapData = byteArrayOutputStream.toByteArray();

                ImageData imageData = ImageDataFactory.create(bitmapData);
                Image logoImage = new Image(imageData);
                logoImage.setHeight(150);
                logoImage.setWidth(150);

                //text font size
                float fontSize = 14f;

                //creating tables for the invoice document
                Table headerTable ,invoiceDetailsTable, purchaseSummaryTable, purchaseAutorizationTable;
                float[] headerTableColumn = {200,200,200,200};
                float[] invoiceDetailsTableColumn = {200,200,200,200};
                float[] purchaseSumamryTableColumn = {160,160,160,160,160};
                float[] purchaseAutorizationTableColumn={200,200,200,200};

                headerTable =new Table(headerTableColumn);
                invoiceDetailsTable= new Table(invoiceDetailsTableColumn);
                purchaseSummaryTable= new Table(purchaseSumamryTableColumn);
                purchaseAutorizationTable= new Table(purchaseAutorizationTableColumn);

                //common invoice layout component
                Color color= new DeviceRgb(200,200,200);
                Color secondaryColor= new DeviceRgb(224,64,251);
                Color purpleColor= new DeviceRgb(128,0,128);
                Color blueColor= ColorConstants.BLUE;
                //populating headerTable data

                logoImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
                Text  sheedah_logo_text = new Text("SHEE_DAH").setTextAlignment(TextAlignment.CENTER).setFontColor(purpleColor).setItalic().setCharacterSpacing(4f);
                Text sheedah_clothing_text=new Text("Clothings").setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.MAGENTA).setItalic().setCharacterSpacing(4f);
                invoiceDetailsTable.addCell( new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));

                //row 1
                invoiceDetailsTable.addCell( new Cell(1,2).setBorder(Border.NO_BORDER).add(new Paragraph( new Text("Invoice No:").setFontColor(purpleColor)).add(new  Text(invoiceNo).setFontColor(ColorConstants.GREEN)).setFontSize(fontSize)));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));

                //row2
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add( new  Paragraph( new Text("Invoice To:").setFontColor(purpleColor)).setFontSize(fontSize)));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                //row 3
                invoiceDetailsTable.addCell(new Cell ().setBorder(Border.NO_BORDER).add(new Paragraph( new Text(customer_name).setBold().setTextRise(5f).setCharacterSpacing(3f).setFontColor(ColorConstants.BLACK)).setFontSize(fontSize)));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                invoiceDetailsTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph(new Text(newDate).setFontColor(ColorConstants.GREEN)).setFontSize(fontSize)));
                invoiceDetailsTable.setBorder(Border.NO_BORDER);

                //populating invoice summary table
                Border summaryDescriptionBorder=new SolidBorder(Border.SOLID);
                summaryDescriptionBorder.setColor(color);
                Border summaryBodyBorder=new DottedBorder(Border.DOTTED);
                summaryBodyBorder.setColor(color);

                //spacing row
                purchaseSummaryTable.addCell( new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                purchaseSummaryTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                purchaseSummaryTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                purchaseSummaryTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));
                purchaseSummaryTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n")));

                //row 1 table description row
                purchaseSummaryTable.addCell(new Cell(1, 2).setBorder(summaryDescriptionBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("PRODUCT").setFontColor(ColorConstants.WHITE).setBold().setFontSize(fontSize)).setBackgroundColor(secondaryColor));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryDescriptionBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("Rate").setFontColor(ColorConstants.WHITE).setBold().setFontSize(fontSize)).setBackgroundColor(secondaryColor));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryDescriptionBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("QTY").setFontColor(ColorConstants.WHITE).setBold().setFontSize(fontSize)).setBackgroundColor(secondaryColor));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryDescriptionBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("AMOUNT").setFontColor(ColorConstants.WHITE).setBold().setFontSize(fontSize)).setBackgroundColor(secondaryColor));

                //row 2 body row
                for (int i=0; i<productTypeList.size(); i++) {
                    purchaseSummaryTable.addCell(new Cell(1, 2).setBorder(summaryBodyBorder).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph(productTypeList.get(i)).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));
                    purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph(amountList.get(i)).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));
                    purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph(quantityList.get(i)).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));
                    Double amount=Double.parseDouble(amountList.get(i) )* Double.parseDouble(quantityList.get(i));
                    purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph(amount.toString()).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));
                }


                //row n to be produced dynamically if more products are purchased

                //nth+1 row
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                //purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                purchaseSummaryTable.addCell(new Cell(1,2).setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("Total").setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph(totalAmount).setFontColor(ColorConstants.DARK_GRAY).setFontSize(fontSize)));

                //nth+2 row
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                //purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).add(new Paragraph("")));
                purchaseSummaryTable.addCell(new Cell(1,2).setBorder(summaryBodyBorder).setBorderTop(Border.NO_BORDER).setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setBackgroundColor(secondaryColor).add(new Paragraph("Balanced Due").setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE).setFontSize(fontSize)));
                purchaseSummaryTable.addCell(new Cell().setBorder(summaryBodyBorder).setBorderLeft(Border.NO_BORDER).setBorderTop(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setBackgroundColor(secondaryColor).add(new Paragraph(debtStatement).setFontColor(ColorConstants.WHITE).setFontSize(fontSize)));


                Text thanksText= new Text("THANKS FOR YOUR PATRONAGE");
                thanksText.setFontColor(ColorConstants.BLACK).setItalic().setCharacterSpacing(3f).setTextAlignment(TextAlignment.LEFT).setFontSize(20f);

                //generating invoice signature image
                Drawable sigImageDraw = getDrawable(R.drawable.sheedah_signature_croped);
                Bitmap sigBitmap=((BitmapDrawable)sigImageDraw ).getBitmap();
                ByteArrayOutputStream OutputStream = new ByteArrayOutputStream();
                sigBitmap
                        .compress(Bitmap.CompressFormat.PNG, 100,OutputStream);
                byte[] bitmapSigData = OutputStream.toByteArray();

                ImageData sigData = ImageDataFactory.create(bitmapSigData);
                Image sigImage = new Image(sigData);
                sigImage.setHeight(100);
                sigImage.setWidth(200);

                //populating purchase autorization table
                //spacing row
                purchaseAutorizationTable.addCell( new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n\n")));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n\n")));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n\n")));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("\n\n")));

                //row 1
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).setBorderBottom(summaryDescriptionBorder).add(sigImage));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                purchaseAutorizationTable.addCell(new Cell(1,2).setBorder(Border.NO_BORDER).add(new Paragraph("Oko-Erin  Junction, Off Taiwo Road, Ilorin, Kwara State.").setFontColor(purpleColor).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSize)).add(new Paragraph("08107330820").setFontColor(ColorConstants.RED).setFontSize(17f).setTextAlignment(TextAlignment.CENTER).setBold()));


                //row 2
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Abdulkadir Motunrayo Rasheedat").setFontColor(purpleColor).setTextAlignment(TextAlignment.CENTER)).add(new Paragraph("(CEO)").setFontColor(purpleColor).setTextAlignment(TextAlignment.CENTER).setFontSize(fontSize)));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));
                //purchaseAutorizationTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));


                //adding table data and other container component to the invoice document
                invoiceDocument.add(logoImage);
                invoiceDocument.add(new Paragraph(sheedah_logo_text).add(sheedah_clothing_text).setTextAlignment(TextAlignment.CENTER).setBold().setFontSize(20f));
                //invoiceDocument.add(headerTable);
                invoiceDocument.add(invoiceDetailsTable);
                invoiceDocument.add(purchaseSummaryTable);
                //adding spacing paragraph before adding the thanksText paragraph for spacing
                invoiceDocument.add(new Paragraph("\n"));
                invoiceDocument.add(new Paragraph(thanksText));
                invoiceDocument.add(purchaseAutorizationTable);
                invoiceDocument.close();
                Toast.makeText(NewInvoicePage.this,"Invoice created",Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, new ShareInvoiceFrag(file)).commit();
                invoiceGenPb.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewInvoicePage.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void  addToPurchaseItem (String amount, String quantity, String amountPaid, String productType) {
        amountList.add(amount);
        quantityList.add(quantity);
        amountPaidList.add(amountPaid);
        productTypeList.add(productType);
    }

    private void   onNewItemBtnClicked () {
       if (!(customerArrayAdapter.isEmpty())) {
           addToPurchaseItem(amount_field.getText().toString(), quantity_field.getText().toString(), amount_paid.getText().toString(), product_category);
           customerChoiceLay.setVisibility(View.GONE);
           amount_field.setText("0.00");
           quantity_field.setText("1");
           amount_paid.setText("0.00");
       }

       else {
           Toast.makeText(this,"Empty customer list, screen will reload!",Toast.LENGTH_SHORT).show();
           onRestart();
       }

    }

    //function that returns the total sum of the items purchased
    private Double getTotalAmount () {
        ArrayList <Double> totalAmountForEachItem= new ArrayList();
        Double totalAmount=0.00;
        for (int i =0; i<productTypeList.size(); i++) {
          totalAmountForEachItem.add(  Double.parseDouble(amountList.get(i)) * Integer.parseInt(quantityList.get(i))) ;
        }

        for (int i =0; i<totalAmountForEachItem.size(); i++) {
            totalAmount= totalAmount+totalAmountForEachItem.get(i);
        }
        return totalAmount;
    }

    View.OnClickListener onClickBackBar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                finishAfterTransition();
        }
    };

    private void  requestForStoragePermission  () {
        //fastly checked if the permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQUEST_CODE);

            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_REQUEST_CODE);
        }
    }


    @Override
    public void finish() {
        startActivity(new Intent(NewInvoicePage.this,HomePageActivity.class));
        super.finish();
    }

    @Override
    public void finishAfterTransition() {
        startActivity(new Intent(NewInvoicePage.this,HomePageActivity.class));
        super.finishAfterTransition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST_CODE) {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                // do nothing
            }
            else {
                Toast.makeText(this,"App won't be able to generate invoice due to denial external storage access",Toast.LENGTH_SHORT).show();
            }
        }
    }
}