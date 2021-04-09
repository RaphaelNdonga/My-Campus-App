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

exports.setAlarm = functions.https.onCall((data,context)=>{
  const setAlarmId = data.setAlarmId
  const topic = data.courseId

  return setAlarm(setAlarmId,topic)
})

exports.cancelAlarm = functions.https.onCall((data,context)=>{
  const cancelAlarmId = data.cancelAlarmId
  const topic = data.courseId

  return cancelAlarm(cancelAlarmId,topic)
})

async function cancelAlarm(cancelAlarmId:string,topic:string) {
  const data = {
    data:{
      cancelAlarmId:cancelAlarmId
    },
  topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The cancelAlarm id ${data.data} to topic ${data.topic} was sent`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred:${error}`)
  })
}
async function setAlarm(setAlarmId:string,topic:string):Promise<void>{
  const data = {
    data:{
      setAlarmId:setAlarmId
    },
    topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The notificationId ${data.data} to topic ${data.topic} was sent successfully`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred when sending data:${error}`)
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