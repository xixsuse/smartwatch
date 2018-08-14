
package com.tcs.sonusourav.smartwatch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

/**
 * Created by SONU SOURAV on 4/1/2018
 */

public class MyPhoneAuth extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MY_Phone_Auth";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [END declare_auth]
    String intent_username;
    String intent_email;
    String intent_dob;
    String intent_gender;
    String intent_password;
    String Phone_with_code;
    Intent intent;
    ProgressBar signProgressbar;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private DatabaseReference userIdRef;
    private FirebaseFirestore firestoreInstance;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView mStatusText;
    private EditText mVerificationField;
    private Button mVerifyButton;
    private Button mResendButton;
    private static long back_pressed;



    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_auth);
        Log.d(TAG, "onCreate: otp layout");



        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }


        intent = getIntent();
        Phone_with_code = intent.getStringExtra("PhoneNumber");
        Log.d("Intent_mobile",Phone_with_code);
        intent_username = intent.getStringExtra("Name");
        intent_email = intent.getStringExtra("Email");
        intent_dob = intent.getStringExtra("DOB");
        intent_gender = intent.getStringExtra("Gender");
        intent_password = intent.getStringExtra("Password");
        Log.d(TAG, "onCreate:MyPhoneAuth " + Phone_with_code);


        signProgressbar = findViewById(R.id.signin_progressBar);
        mStatusText = findViewById(R.id.sign_up_msg);

        mVerificationField = findViewById(R.id.otp_et);
        TextView mMobileNumber = findViewById(R.id.tv_mobile_no_display);

        mVerifyButton = findViewById(R.id.verify_button);
        mResendButton = findViewById(R.id.resend_button);


        // Assign click listeners
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mMobileNumber.setText(Phone_with_code);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        rootRef = firebaseInstance.getReference("Users");
        userIdRef = firebaseInstance.getReference("UserId");

        Log.d("start","true");
        // [END initialize_auth]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the sign_up_phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the sign_up_phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sign_up_phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(getApplicationContext(), "Invalid Phone number", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), "Invalid Phone number",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided sign_up_phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };


        Log.d("Phone Verification", "OTP initiated:" );

        startPhoneNumberVerification(Phone_with_code);

        Log.d("Phone Verification", "OTP done:" );

    }

   /* public void onResume() {
        super.onResume();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        if (currentUser != null) {
            signProgressbar.setVisibility(View.VISIBLE);
            mMobileNumber.setText(Phone_with_code);
            mMobileNumber.setFocusable(false);
            startPhoneNumberVerification(Phone_with_code);
        }

    }*/

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        // [START_EXCLUDE]
        if (mVerificationInProgress) {
            startPhoneNumberVerification(Phone_with_code);
        }
        // [END_EXCLUDE]
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    public void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }
   /* private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }*/

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]


                            //email auth started

                            mAuth.createUserWithEmailAndPassword(intent_email, intent_password)
                                    .addOnCompleteListener(MyPhoneAuth.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Toast.makeText(MyPhoneAuth.this, "User "+ intent_email+ " successfully created", Toast.LENGTH_SHORT).show();

                                            if (task.isSuccessful()) {


                                                final UserClass user = new UserClass(intent_username, intent_email, intent_dob, intent_gender, Phone_with_code, intent_password);
                                                userIdRef.push().setValue(Phone_with_code);
                                                rootRef.push().setValue(encodeUserEmail(intent_email));
                                                rootRef.child(encodeUserEmail(intent_email)).setValue(user);




                                                Intent intent = new Intent(MyPhoneAuth.this, SignInActivity.class);
                                                startActivity(intent);


                                            }


                                            if (!task.isSuccessful()) {
                                                signProgressbar.setVisibility(View.GONE);

                                                Toast.makeText(MyPhoneAuth.this, "User creation failed \n\n" + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MyPhoneAuth.this, SignUpActivity.class);
                                                startActivity(intent);
                                            }
                                        }

                                    });




                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Phone Authentication failed", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDED


                            Intent intent = new Intent(MyPhoneAuth.this, SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the sign_up_phone number field and start button

                disableFocus(mResendButton);

                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableFocus(mResendButton);



                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableFocus(mVerifyButton, mResendButton);

                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in

                disableFocus(mVerifyButton, mResendButton);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                        disableFocus(mVerifyButton);
                        mVerificationField.setFocusable(false);


                    } else {
                        mVerificationField.setText(R.string.auto);
                        disableFocus(mVerifyButton);
                        mVerificationField.setFocusable(false);


                    }
                }
                signProgressbar.setVisibility(View.VISIBLE);


                break;
            case STATE_SIGNIN_FAILED:

                // No-op, handled by sign-in check
                enableFocus(mVerifyButton, mResendButton);
                signProgressbar.setVisibility(View.GONE);


                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                enableFocus(mVerifyButton, mResendButton);
                break;
        }


    }

    private void enableFocus(Button... buttons) {
        for (Button v : buttons) {
            v.setFocusable(true);
        }
    }

    private void disableFocus(Button... buttons) {
        for (Button v : buttons) {
            v.setFocusable(false);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_up:
                startPhoneNumberVerification(Phone_with_code);
                break;
            case R.id.verify_button:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mStatusText.setVisibility(View.VISIBLE);
                    mStatusText.setText("Cannot be empty !!");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                mVerificationField.setFocusable(false);
                signProgressbar.setVisibility(View.VISIBLE);
                break;
            case R.id.resend_button:
                resendVerificationCode(Phone_with_code, mResendToken);
                Toast.makeText(getApplicationContext(), "OTP resent", Toast.LENGTH_SHORT).show();
                signProgressbar.setVisibility(View.GONE);

                break;

        }
    }

}



