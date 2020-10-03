import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

exports.addAdminCourseId = functions.https.onCall((data,context)=>{
  functions.logger.info("on call has been called",{structuredData:true})
  const email = data.email;
  const courseId = data.courseId;
  return setAdminCourseId(email,courseId).then(()=>{
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

async function setAdminCourseId(email:string,courseId:string): Promise<void> {
  functions.logger.info(`The email is ${email}`)
  const user = await admin.auth().getUserByEmail(email);

  if(user.customClaims && (user.customClaims as any).admin === true){
    functions.logger.info("This is an already registered user",{structuredData: true})
    return 
  }
  functions.logger.info(`setting ${email} as administrator with course id ${courseId}`,{structuredData:true})
  return admin.auth().setCustomUserClaims(user.uid,{
    admin:true,
    courseId:courseId
  })
}

async function setCourseId(email:string,courseId:string): Promise<void>{
  const user = await admin.auth().getUserByEmail(email)

  if(user.customClaims && (user.customClaims as any).courseId === courseId){
    functions.logger.info("This user already has a course id ",{structuredData: true})
    return
  }
  functions.logger.info(`setting the course id to ${courseId}`,{structuredData:true})
  return admin.auth().setCustomUserClaims(user.uid,{
    courseId:courseId
  })
}