package com.apollo.pharmacy.ocr.activities.insertprescriptionnew.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.apollo.pharmacy.ocr.R;

import java.io.File;
import java.util.List;

public class PrescriptionViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imagePathList;

    public PrescriptionViewPagerAdapter(Context context, List<String> imagePathList) {
        this.context = context;
        this.imagePathList = imagePathList;
    }

    @Override
    public int getCount() {
        return imagePathList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adapter_prescription_view_pager, null);
        String imagePath = imagePathList.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.prescription_view_pager);
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            imageView.setImageURI(uri);
        }

        imageView.setOnClickListener(view1 -> {

        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
