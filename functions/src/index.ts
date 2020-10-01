import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase Raphael!");
// });

exports.addAdmin = functions.https.onCall((data,context)=>{
  functions.logger.info("on call has been called",{structuredData:true})
  const email = data.email;
  return grantAdminRole(email).then(()=>{
    return{
      result:`Request fulfilled! ${email} is now an administrator`
    }
  })
})

exports.addCourseId = functions.https.onCall((data,context)=>{
  const courseId = data.courseId
  const email = data.email
  return setCourseId(email,courseId).then(()=>{
    return {
      result:`Request fulfilled! Group id ${courseId} set`
    }
  })
})

async function grantAdminRole(email:string): Promise<void> {
  functions.logger.info(`The email is ${email}`)
  const user = await admin.auth().getUserByEmail(email);

  if(user.customClaims && (user.customClaims as any).admin === true){
    functions.logger.info("This is an already registered user",{structuredData: true})
    return 
  }
  functions.logger.info(`setting ${email} as administrator`,{structuredData:true})
  return admin.auth().setCustomUserClaims(user.uid,{
    admin:true
  })
}

async function setCourseId(email:string,courseId:string): Promise<void>{
  functions.logger.info(`setting group id to ${{courseId}}`)
  const user = await admin.auth().getUserByEmail(email)

  functions.logger.info(`setting the course id to ${courseId}`,{structuredData:true})

  return admin.auth().setCustomUserClaims(user.uid,{
    courseId:courseId
  })
}