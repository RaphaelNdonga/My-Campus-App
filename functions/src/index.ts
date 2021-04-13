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

exports.sendTodayTimetableId = functions.https.onCall((data,context)=>{
  const timetableId = data.timetableId
  const topic = data.courseId

  return sendTodayTimetableId(timetableId,topic)
})

exports.sendTomorrowTimetableId = functions.https.onCall((data,context)=>{
  const timetableId = data.timetableId
  const topic = data.courseId

  return sendTomorrowTimetableId(timetableId,topic)
})
exports.cancelTomorrowAlarm = functions.https.onCall((data,context)=>{
  const requestCode = data.requestCode
  const subject = data.subject
  const topic = data.courseId

  return cancelTomorrowAlarm(requestCode,subject,topic)
})

async function cancelTomorrowAlarm(requestCode:string,subject:string,topic:string) {
  const data = {
    data:{
      tomorrowRequestCode:requestCode,
      tomorrowCancelledSubject:subject
    },
  topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The tomorrow cancelAlarm id ${data.data} to topic ${data.topic} was sent`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred:${error}`)
  })
}
exports.cancelTodayAlarm = functions.https.onCall((data,context)=>{
  const requestCode = data.requestCode
  const subject = data.subject
  const topic = data.courseId

  return cancelTodayAlarm(requestCode,subject,topic)
})

async function cancelTodayAlarm(requestCode:string,subject:string,topic:string) {
  const data = {
    data:{
      todayRequestCode:requestCode,
      todayCancelledSubject:subject
    },
  topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The cancelAlarm id ${data.data} to topic ${data.topic} was sent`)
  }).catch((error)=>{
    functions.logger.error(`An error occurred:${error}`)
  })
}

async function sendTomorrowTimetableId(timetableId:string,courseId:string):Promise<void>{
  const data = {
    data:{
      tomorrowTimetableId:timetableId
    },
    topic:courseId
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The todayId ${data.data} to topic ${data.topic} was sent successfully`)
  })
}

async function sendTodayTimetableId(timetableId:string,topic:string):Promise<void>{
  const data = {
    data:{
      todayTimetableId:timetableId
    },
    topic:topic
  }
  return admin.messaging().send(data).then((response)=>{
    functions.logger.info(`The todayId ${data.data} to topic ${data.topic} was sent successfully`)
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