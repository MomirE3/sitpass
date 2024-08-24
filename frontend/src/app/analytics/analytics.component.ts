import { Component, OnInit } from '@angular/core';
import { EChartsOption } from 'echarts';
import { AnalyticsService } from '../services/analytics.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit {
  public chartOptions: EChartsOption = {
    xAxis: {
      type: 'category',
      data: []  
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'User Visits',
        type: 'bar',
        data: []  
      },
      {
        name: 'Reviews',
        type: 'bar',
        data: [] 
      }
    ]
  };

  public selectedTimeframe: string = 'weekly';
  public startDate?: string;
  public endDate?: string;
  public analyticsData: any;
  private facilityId!: number;

  constructor(
    private analyticsService: AnalyticsService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.facilityId = +this.route.snapshot.paramMap.get('id')!;
    this.fetchAnalyticsData();
  }

  fetchAnalyticsData(): void {
    switch (this.selectedTimeframe) {
      case 'weekly':
        this.analyticsService.getWeeklyAnalytics(this.facilityId).subscribe(data => this.updateAnalyticsData(data));
        break;
      case 'monthly':
        this.analyticsService.getMonthlyAnalytics(this.facilityId).subscribe(data => this.updateAnalyticsData(data));
        break;
      case 'yearly':
        this.analyticsService.getYearlyAnalytics(this.facilityId).subscribe(data => this.updateAnalyticsData(data));
        break;
      case 'custom':
        if (this.startDate && this.endDate) {
          this.analyticsService.getCustomAnalytics(this.facilityId, this.startDate, this.endDate).subscribe(data => this.updateAnalyticsData(data));
        }
        break;
      default:
        break;
    }
  }

  updateAnalyticsData(data: any): void {
    this.analyticsData = data;
    
    if (Array.isArray(this.chartOptions.series)) {
      this.chartOptions.xAxis = {
        type: 'category',
        data: data.hourlyActivity.map((activity: any) => `Hour ${activity[0]}`) 
      };
      
      this.chartOptions.series[0].data = data.hourlyActivity.map((activity: any) => activity[1]);  
      this.chartOptions.series[1].data = data.hourlyActivity.map((activity: any) => activity[1]); 
    }
    this.chartOptions = { ...this.chartOptions };
  }
}
