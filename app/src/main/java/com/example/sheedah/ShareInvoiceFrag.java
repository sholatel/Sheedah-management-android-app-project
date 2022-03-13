package com.example.sheedah;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ShareInvoiceFrag  extends Fragment {
    File file;
    Uri uri;
    LinearLayout shareInvoiceBtn;


    public ShareInvoiceFrag (File file) {
        super(R.layout.share_pdf_fragment_layout);
        this.file=file;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareInvoiceBtn=view.findViewById(R.id.shareBtn);
        shareInvoiceBtn.setOnClickListener(shareInvoiceBtnClicked);

    }

    View.OnClickListener shareInvoiceBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.N) {
                uri= FileProvider.getUriForFile( ShareInvoiceFrag.this.getActivity(), ShareInvoiceFrag.this.getActivity().getPackageName()+".provider", file);
            }
            else
                uri=Uri.fromFile(file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setPackage("com.whatsapp");
            startActivity(shareIntent);
            //ShareInvoiceFrag.this.getActivity().finish();
        }
    };
}
