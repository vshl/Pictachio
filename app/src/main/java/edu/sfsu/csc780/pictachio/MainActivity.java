package edu.sfsu.csc780.pictachio;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    LocalCamera lc = new LocalCamera(this);
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView mImageView;

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain DrawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Enable Drawer toggle actions
        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.drawer_opened,
                R.string.drawer_closed);
        drawerLayout.setDrawerListener(drawerToggle);

        NavigationView navi = (NavigationView) findViewById(R.id.navi);
        setupDrawerContent(navi);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            CameraRollFragment cameraRollFragment = new CameraRollFragment();
            cameraRollFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cameraRollFragment).commit();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setBtnListenerOrDisable(
                fab,
                lc.mTakePhotoOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
    }

    private void setupDrawerContent(NavigationView navi) {
        navi.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.item_camera_roll:
                fragmentClass = CameraRollFragment.class;
                break;
            case R.id.item_albums:
                fragmentClass = GalleryFragment.class;
                break;
            default:
                fragmentClass = CameraRollFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            lc.handleCameraPhoto();
            CameraRollFragment cameraRollFragment = (CameraRollFragment)
                    getFragmentManager().findFragmentById(R.id.fragment_container);

            if (cameraRollFragment != null) {
                cameraRollFragment.updateAdapter();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            CameraRollFragment cameraRollFragment = (CameraRollFragment)
                    getFragmentManager().findFragmentById(R.id.fragment_container);
            if (cameraRollFragment != null)
                cameraRollFragment.updateAdapter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Hamburger icon and sync with drawer
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void setBtnListenerOrDisable(
            FloatingActionButton btn,
            FloatingActionButton.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setClickable(false);
        }
    }
}
