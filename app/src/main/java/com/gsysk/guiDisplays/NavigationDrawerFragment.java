package com.gsysk.guiDisplays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.gsysk.constants.ConstantValues;
import com.gsysk.fma.R;
import com.gsysk.phoneUtils.PhoneFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*if(position==0){
                    Log.d("MyApp", "List item zero clicked");
                    getRoute();
                }else if(position==1){
                    Log.d("MyApp","List item 1 clicked");
                    getStopdetails();
                }else */if(position==0){
                    getAdminDetails();
                }else if(position==1){
                    getUserDetails();
                }/*else if(position==2){
                    CheckConfirm();
                }*/else if(position==2){
                    logout();
                }

                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[]{
                        /*getString(R.string.title_section1),
                        getString(R.string.title_section2),*/
                        getString(R.string.title_section3),
                        getString(R.string.title_section4),
                        //getString(R.string.title_section5),
                        getString(R.string.title_section6),
                }));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        System.out.print(position);
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

       /* if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    /*public void getRoute(){
        Toast.makeText(getActivity(),
                "Get Route Details", Toast.LENGTH_SHORT)
                .show();
    }*/

    /*public void getStopdetails(){
        Toast.makeText(getActivity(),
                "Get Droppoint Details", Toast.LENGTH_SHORT)
                .show();
    }*/

    public void getAdminDetails(){
      /*  Toast.makeText(getActivity(),
                "Get admin Details", Toast.LENGTH_SHORT)
                .show();*/

        String content = PhoneFunctions.getFromPrivateSharedPreferences(getActivity(), "adminForDriver");
        Log.d("MyApp_adtoDr",content);
        if(content==null || content.equals("Not Found"))
        {
            ToastMessageHelper.displayLongToast(getActivity(), ConstantValues.PLEASE_TRY_AGAIN);
        }
        else
        {
            String titleMessage = "";

            titleMessage = "Admins : ";


            AlertDialogHelper createdDialog = new AlertDialogHelper(getActivity(), titleMessage);
            createdDialog.createListAlertDialog(content);
        }
    }

    public void getUserDetails(){
      /*  Toast.makeText(getActivity(),
                "Get user Details", Toast.LENGTH_SHORT)
                .show();*/

   /*     String content = PhoneFunctions.getFromPrivateSharedPreferences(getActivity(), "UsersToDriver");
        Log.d("MyApp_Usertod",content);
        if(content==null || content.equals("Not Found"))
        {
            ToastMessageHelper.displayLongToast(getActivity(), ConstantValues.PLEASE_TRY_AGAIN);
        }
        else
        {
            String titleMessage = "";

            titleMessage = "Users : ";


            AlertDialogHelper createdDialog = new AlertDialogHelper(getActivity(), titleMessage);
            createdDialog.createListAlertDialog(content);
        }*/

        String content = PhoneFunctions.getFromPrivateSharedPreferences(getActivity(), "usersToDriverwithDPName");
        Log.d("MyApp_Usertod",content);

        String uDetails = content.split(" ; ")[0];
        Log.d("MyApp_Usertod",uDetails);
        String[] uDet = uDetails.split(" .,");

        String[] nameArray = new String[uDet.length];
        final String[] phoneNumArray = new String[uDet.length];
        String[] dropPointArray  = new String[uDet.length];
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        String[] from = { "mainTxt","subTxt","subTxt2" };
        int[] to = { R.id.mainTxt,R.id.subTxt,R.id.subTxt2};
        int k=0;


        for(String s:uDet){
            nameArray[k] = s.split(" / ")[0];
            phoneNumArray[k] = s.split(" / ")[1];
            dropPointArray[k] = s.split(" / ")[2];
            k++;
        }


        for(int i=0;i<uDet.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("mainTxt", nameArray[i]);
            hm.put("subTxt",phoneNumArray[i]);
            hm.put("subTxt2",dropPointArray[i]);
            aList.add(hm);
        }




        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Users : ");
        Dialog dialog = null;
        ListView modeList = new ListView(getActivity());


        SimpleAdapter modeAdapter = new SimpleAdapter(getActivity(), aList, R.layout.listview_dialog_layout, from, to);

        int[] colors = {0, 0xFFFF0000, 0}; // red for the example
        modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        modeList.setDividerHeight(2);
        modeList.setAdapter(modeAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {

                /*PhoneFunctions phoneFunctions = new PhoneFunctions(getActivity());
                phoneFunctions.makePhoneCall(phoneNumArray,position);*/

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumArray[position]));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(callIntent);

            }
        };

        // Setting the item click listener for the listview
        modeList.setOnItemClickListener(itemClickListener);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(dialog!=null)
                {
                    dialog.dismiss();
                }
            }
        });
        builder.setView(modeList);
        dialog = builder.create();


        dialog.show();

    }

   /* public void CheckConfirm(){
        Toast.makeText(getActivity(),
                "Check Confirmation", Toast.LENGTH_SHORT)
                .show();
    }*/



    public void logout(){
        PhoneFunctions.storeInPrivateSharedPreferences(getActivity(),"savedUsername","Empty");
        PhoneFunctions.storeInPrivateSharedPreferences(getActivity(),"savedPassword","Empty");

        getActivity().finish();
    }
}
