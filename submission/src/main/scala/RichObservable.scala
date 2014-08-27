import rx.lang.scala._
import scala.concurrent._

object Rich {

  implicit class RichObservable[A](observable: Observable[A]) {

    def first(implicit ec: ExecutionContext): Future[A] = {
      val p = Promise[A]()
      val observer = Observer[A](
        onNext = (v: A) => {
          p.success(v)
          ()
        },
        onError = (e: Throwable) => {
          p.failure(e)
          ()
        },
        onCompleted = () => {
          p.failure(new IllegalArgumentException("observable completed"))
          ()
        })
      val sub = observable.subscribe(observer)
      p.future andThen {
        case _ => sub.unsubscribe()
      }
    }

    def futureSeq(implicit ec: ExecutionContext): Future[Seq[A]] = {
      val p = Promise[Seq[A]]()
      val buf = collection.mutable.ListBuffer[A]()
      val sub = observable.subscribe(
        onNext = (v: A) => {
          buf += v
        },
        onError = (e: Throwable) => {
          p.failure(e)
        },
        onCompleted = () => {
          p.success(buf.toSeq)
        })
      p.future andThen { case _ => sub.unsubscribe() }
    }

  }

}