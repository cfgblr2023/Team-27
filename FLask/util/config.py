
import firebase_admin
from firebase_admin import credentials

cred = credentials.Certificate("util/team27-391514-firebase-adminsdk-557j2-9ca31e701c.json")
app = firebase_admin.initialize_app(cred, {
    'storageBucket': 'team27-391514.appspot.com'
},name='storage')