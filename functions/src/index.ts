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

exports.checkIfAdminExists = functions.https.onCall((data,context)=>{
  const courseId = data.courseId
  return checkIfAdminExists(courseId).then((Boolean)=>{
    return{
      result:Boolean
    }
  })
})

exports.sendMessage = functions.https.onCall((data,context)=>{
  const message = data.message
  const topic = data.courseId
  
  return sendMessage(message,topic)
})

exports.updateData = functions.https.onCall((data,context)=>{
  const timetableId = data.timetableId
  const dayOfWeek = data.dayOfWeek
  const topic = data.courseId

  return updateData(timetableId,dayOfWeek,topic)
})

exports.updateAssessmentData = functions.https.onCall((data,context)=>{
  const assessmentId = data.assessmentId
  const assessmentType = data.assessmentType
  const topic = data.courseId

  return updateAssessmentData(assessmentId,assessmentType,topic)
})

exports.cancelData = functions.https.onCall((data,context)=>{
  const requestCode = data.requestCode
  const subject = data.subject
  const dayOfWeek = data.dayOfWeek
  const topic = data.courseId

  return cancelData(requestCode,subject,dayOfWeek,topic)
})

exports.cancelAssessmentData = functions.https.onCall((data,context)=>{
  const requestCode = data.requestCode
  const subject = data.subject
  const assessmentType = data.assessmentType
  const topic = data.courseId

  return cancelAssessmentData(requestCode,subject,assessmentType,topic)
})

exports.upgradeToAdmin = functions.https.onCall((data,context)=>{
  const email = data.email
  const courseId = data.courseId

  return upgradeToAdmin(email,courseId)
})

async function upgradeToAdmin(email:string,courseId:string){
  const user = await admin.auth().getUserByEmail(email)
  return admin.auth().setCustomUserClaims(user.uid,{
    admin:true,
    courseId:courseId
  }).then((response)=>{functions.logger.info(`Successfully upgraded ${email} to an admin`)})
}

async function cancelAssessmentData(requestCode:string,subject:string,assessmentType:string,topic:string){
  const data = {
    data:{
      assessmentRequestCode:requestCode,
      assessmentSubject:subject,
      assessmentType:assessmentType
    },
    topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`cancel data was sent successfully ${data.data} to topic ${data.topic}`)
  }).catch((error)=>{
    functions.logger.error(`error occurred ${error}`)
  })
}

async function cancelData(requestCode:string,subject:string,dayOfWeek:string,topic:string) {
  const data = {
    data:{
      requestCode:requestCode,
      cancelSubject:subject,
      cancelDay:dayOfWeek
    },
    topic:topic
  }

  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`cancel data was sent successfully ${data.data} to topic ${data.topic}`)
  }).catch((error)=>{
    functions.logger.error(`error occurred ${error}`)
  })
}

async function updateData(timetableId:string,dayOfWeek:string,topic:string){
  const data = {
    data:{
      updateId:timetableId,
      updateDay:dayOfWeek,
    },
    topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The update alarm id ${data.data} to topic ${data.topic} was sent successfully`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred ${error}`)
  })
}

async function updateAssessmentData(assessmentId:string, assessmentType:string, topic:string){
  const data = {
    data:{
      updateAssessmentId:assessmentId,
      updateAssessmentType:assessmentType
    },
    topic:topic
  }

  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The update assessment id ${data.data} to topic ${data.topic} was sent successfully`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred ${error}`)
  })
}

async function sendMessage(message:string,topic:string):Promise<void>{
  const data = {
    data:{
      message:message
    },
    topic: topic
  };
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The message ${data.data} to topic ${data.topic} was sent successfully`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred when sending data: ${error}`)
  })
}

async function checkIfAdminExists(courseId:String):Promise<boolean>{
  return admin.firestore().collection("courses/"+courseId+"/admins").get().then(snapshot=>{
    return snapshot.size > 0 
  })
}

async function setAdminCourseId(email:string,courseId:string): Promise<void> {
  functions.logger.info(`The email is ${email}`)
  const user = await admin.auth().getUserByEmail(email);
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