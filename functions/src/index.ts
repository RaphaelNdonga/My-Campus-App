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
// export const getComputerScienceAdmins = functions.https.onRequest((request,response)=>{
//   admin.firestore().doc('courses/Bsc Computer Science/admins/lemayian@gmail.com').get()
//     .then(snapshot=>{
//     const data = snapshot.data()
//     response.send(data)
//     })
//       .catch(error=>{
//         console.log(error)
//         response.status(500).send(error)
//       })
// })

// export const confirmAdminExists = functions.https.onRequest((request,response)=>{
//   admin.firestore().collection('courses/Bsc Water Science/admins').get()
//   .then(snapshot=>{
//     const exists = snapshot.size > 0
//     response.send("The snapshot existing is "+ exists + "because snapshot size is "+ snapshot.size)
//   }).catch(error=>{
//     console.log(error)
//     response.status(500).send(error)
//   })
// })