package settings.hometech.com.myapplication;

import android.app.Fragment;

/**
 * Created by yimin on 2017-05-13.
 */
public class FragmentFactory {
    private  Fragment fragment = null;;
    public Fragment getInstanceByIndex(int index) {
        switch (index) {
            case 1:
                fragment = new NetworkManagerOneFragment();
                break;
            case 2:
                fragment = new NetworkManagerTwoFragment();
                break;
        }
        return fragment;
    }
}
