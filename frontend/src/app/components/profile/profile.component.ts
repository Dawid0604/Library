import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../services/storage.service';
import { UserService } from '../../services/user.service';
import { UserDetails } from '../../model/UserDetails';
import { UserReaction } from '../../model/UserReaction';
import { ReactionService } from '../../services/reaction.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  currentUser: UserDetails = {}  as UserDetails;
  userReactions: UserReaction[] = [];

  defaultImage: string = '../../../assets/User-avatar.svg.png';
  errorMessage = '';

  showReactionsFlag: boolean = true;
  showEditProfileFlag: boolean = false;
  currentPasswordIsNotSameValidationFlag: boolean = false;
  newPasswordIsLowerThanSixCharactersValidationFlag: boolean = false;

  form: any = {
    newPassword: null,
    newAvatar: null
  };

  constructor(private userService: UserService,
              private authService: AuthService,
              private reactionService: ReactionService,
              private storageService: StorageService,
              private router: Router) { }

  ngOnInit(): void {
    this.loadDetails();

    this.reactionService
        .getUserReactions()
        .subscribe({
          next: data => {
            this.userReactions = data
            this.showEditProfileFlag = true;
            this.showReactionsFlag = (this.userReactions && this.userReactions.length > 0);
          },
          error: err => console.log(err)
        })
  }

  private loadDetails() {
    this.userService
        .getDetails()
        .subscribe({
          next: data => this.currentUser = data,
          error: err => console.log(err)
        })
  }

  showLastReactionsBox(): void {
    this.showReactionsFlag = true;
    this.showEditProfileFlag = false;
  }

  showEditAvatarBox(): void {
    this.showEditProfileFlag = true;
    this.showReactionsFlag = false;
  }

  deleteProfile(): void {
    if(confirm("You want delete account. Are you sure?")) {
      this.authService.delete()
                      .subscribe({
                        next: data => {
                          this.storageService.logout();
                          window.location.href = "login";
                        },
                        error: err => console.log(err)
                      })
    }
  }

  handleImageError(event: any) {
    event.target.src = this.defaultImage;
  }

  onSubmit() {
    const { newPassword, newAvatar } = this.form;

    if(newPassword && (newPassword as string).length < 6) {
      this.newPasswordIsLowerThanSixCharactersValidationFlag = true;
      return;

    } else {
      this.newPasswordIsLowerThanSixCharactersValidationFlag = false;
    }

    if(newPassword || newAvatar) {
      this.userService
          .update({
            password: newPassword,
            avatar: newAvatar
          })
          .subscribe({
            next: data => {
              this.form.newPassword = null;
              this.form.newAvatar = null;
              this.loadDetails();
            },
            error: err => console.log(err)
          })
    }
  }
}
