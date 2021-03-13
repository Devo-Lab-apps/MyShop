const functions = require("firebase-functions");
const admin = require('firebase-admin');
admin.initializeApp()


exports.importPages = functions.firestore.document('users/{userId}').onCreate(async (snap, context) => {
    const user = snap.data()
    const accountId = user.accountId
    console.log(`User created: Email: ${user.email} AccountId: ${accountId}`)
    let userCollection = admin.firestore().collection('account').doc(accountId).collection('notebook').doc('foreign')
    const doc = await userCollection.get()
    if (!doc.exists) {
        console.log(`Account doesn't exist for the user: ${user}`)
        return
    }
    let docs = await admin.firestore().collection('page').where('consumerUserId', '==', user.email).orderBy('consumerUserId').orderBy('pageId').limit(10).get()
    while (docs._size > 0) {
        let lastDoc = docs.docs[docs.docs.length - 1]
        let qs = docs.docs
        const pageIds = qs.map(d => {
            return d.data().pageId
        })
        await userCollection.update({
            'pages': admin.firestore.FieldValue.arrayUnion(...pageIds)
        })
        if (lastDoc) docs = await admin.firestore().collection('page').where('consumerUserId', '==', user.email).orderBy('consumerUserId').orderBy('pageId').startAfter(lastDoc).limit(1).get()
        else break
    }
});