package com.aslan.contra.sensor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gobinath on 10/27/15.
 */
public class ContactsSensor {
    private Context context;
    private List<String> contacts;
    private ContentResolver cr;

    public ContactsSensor(Context context) {
        this.context = context;
        this.contacts = new ArrayList<>();
        this.cr = context.getContentResolver();
    }

    public List<String> collect() {
//        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//        Log.i("No of contacts", "" + cursor.getCount());
//        if (cursor.moveToFirst()) {
//
//            do {
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//
//                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        contacts.add(contactNumber);
//                        break;
//                    }
//                    pCur.close();
//                }
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();

        queryAllRawContacts();
        return contacts;
    }

    private void queryAllRawContacts() {

        final String[] projection = new String[]{
                ContactsContract.RawContacts.CONTACT_ID,                    // the contact id column
                ContactsContract.RawContacts.DELETED                        // column if this contact is deleted
        };

        final Cursor rawContacts = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,                // the uri for raw contact provider
                projection,
                null,                                    // selection = null, retrieve all entries
                null,                                    // not required because selection does not contain parameters
                null);                                    // do not order

        final int contactIdColumnIndex = rawContacts.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
        final int deletedColumnIndex = rawContacts.getColumnIndex(ContactsContract.RawContacts.DELETED);

        if (rawContacts.moveToFirst()) {                    // move the cursor to the first entry
            while (!rawContacts.isAfterLast()) {            // still a valid entry left?
                final int contactId = rawContacts.getInt(contactIdColumnIndex);
                final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);
                if (!deleted) {
                    queryAllPhoneNumbersForContact(contactId);
                }
                rawContacts.moveToNext();                // move to the next entry
            }
        }

        rawContacts.close();
    }

    public void queryAllPhoneNumbersForContact(int contactId) {
        final String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.CommonDataKinds.Phone.TYPE,
        };

        final Cursor phone = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)},
                null);

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (!phone.isAfterLast()) {
                final String number = phone.getString(contactNumberColumnIndex);
                contacts.add(number);
                phone.moveToNext();
            }

        }
        phone.close();
    }



/*
    public void queryAllEmailAddressesForContact(int contactId, List<ListViewEntry> content) {
        final String[] projection = new String[] {
                Email.DATA,							// use Email.ADDRESS for API-Level 11+
                Email.TYPE
        };

        final Cursor email = managedQuery(
                Email.CONTENT_URI,
                projection,
                Data.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)},
                null);

        if(email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(Email.TYPE);

            while(!email.isAfterLast()) {
                final String address = email.getString(contactEmailColumnIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                content.add(new ListViewEntry(address, Email.getTypeLabelResource(type),R.string.type_email));
                email.moveToNext();
            }

        }
        email.close();
    }

    private SpinnerEntry queryDetailsForContactSpinnerEntry(int contactId) {
		final String[] projection = new String[] {
				Contacts.DISPLAY_NAME,					// the name of the contact
				Contacts.PHOTO_ID						// the id of the column in the data table for the image
		};

		final Cursor contact = managedQuery(
				Contacts.CONTENT_URI,
				projection,
				Contacts._ID + "=?",						// filter entries on the basis of the contact id
				new String[]{String.valueOf(contactId)},	// the parameter to which the contact id column is compared to
				null);

		if(contact.moveToFirst()) {
			final String name = contact.getString(
					contact.getColumnIndex(Contacts.DISPLAY_NAME));
			final String photoId = contact.getString(
					contact.getColumnIndex(Contacts.PHOTO_ID));
			final Bitmap photo;
			if(photoId != null) {
				photo = queryContactBitmap(photoId);
			} else {
				photo = null;
			}
			contact.close();
			return new SpinnerEntry(contactId, photo, name);
		}
		contact.close();
		return null;
	}

	private Bitmap queryContactBitmap(String photoId) {
		final Cursor photo = managedQuery(
				Data.CONTENT_URI,
				new String[] {Photo.PHOTO},		// column where the blob is stored
				Data._ID + "=?",				// select row by id
				new String[]{photoId},			// filter by the given photoId
				null);

		final Bitmap photoBitmap;
		if(photo.moveToFirst()) {
			byte[] photoBlob = photo.getBlob(
					photo.getColumnIndex(Photo.PHOTO));
			photoBitmap = BitmapFactory.decodeByteArray(
					photoBlob, 0, photoBlob.length);
		} else {
			photoBitmap = null;
		}
		photo.close();
		return photoBitmap;
	}


    */
}
