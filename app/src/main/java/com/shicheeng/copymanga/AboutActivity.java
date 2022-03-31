package com.shicheeng.copymanga;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drakeet.about.AbsAboutActivity;
import com.drakeet.about.Card;
import com.drakeet.about.Category;
import com.drakeet.about.License;

import java.util.List;

public class AboutActivity extends AbsAboutActivity {
    @SuppressLint({"SetTextI18n", "ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.mipmap.ic_launcher_copy);
        slogan.setText(R.string.li_des);
        version.setText("v "+BuildConfig.VERSION_NAME);

    }

    @Override
    protected void onItemsCreated(@NonNull List<Object> items) {
        items.add(new Category(getString(R.string.description_text)));
        items.add(new Card(getString(R.string.des)));


        items.add(new Category(getString(R.string.open_source)));
        items.add(new License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("gson","google",License.APACHE_2,"https://github.com/google/gson"));
        items.add(new License("okhttp3","square",License.APACHE_2,"https://github.com/square/okhttp"));
        items.add(new License("glide","bumptech",
                "BSD, part MIT and Apache 2.0. See the LICENSE file for details",
                "https://github.com/bumptech/glide"));
        items.add(new License("subsampling-scale-image-view","davemorrissey",
                License.APACHE_2,"https://github.com/davemorrissey/subsampling-scale-image-view"));
        items.add(new License("Material Components for Android","material-components",License.APACHE_2,
                "https://github.com/material-components/material-components-android"));

    }
}
